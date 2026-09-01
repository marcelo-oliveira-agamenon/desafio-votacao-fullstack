# Votação

API REST + front-end para gerenciar pautas e sessões de votação de
assembleias: cadastrar pautas, abrir sessões por tempo determinado,
receber votos `SIM`/`NAO` (um por associado) e apurar o resultado.

O enunciado original está em [CHALLENGE.md](CHALLENGE.md).

## Stack

- **Backend**: Java 21, Spring Boot 4, Spring Data JPA, Flyway, PostgreSQL
- **Frontend**: React 19, Vite, TypeScript, Tailwind CSS
- **Testes**: JUnit 5, Mockito, Testcontainers
- **Carga**: k6

## Como executar

Pré-requisitos: JDK 21, Docker (para o banco e os testes de integração),
Node 20+.

### 1. Banco de dados

```bash
docker compose up -d db
```

Sobe um PostgreSQL 16 em `localhost:5432` (`votacao`/`votacao`/`votacao`)
com volume nomeado — os dados sobrevivem a `restart` e `down`.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Disponível em `http://localhost:8080`. O Flyway cria o schema no primeiro
start. Variáveis de ambiente (todas com default para o compose acima):

| Var | Default | |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/votacao` | |
| `DB_USERNAME` / `DB_PASSWORD` | `votacao` / `votacao` | |
| `SERVER_PORT` | `8080` | |
| `ELEGIBILIDADE_PROB_CPF_INVALIDO` | `0.2` | chance de o client fake tratar o CPF como não encontrado (Bônus 1) |
| `ELEGIBILIDADE_PROB_INELEGIVEL` | `0.2` | chance de `UNABLE_TO_VOTE` |

> Para testar o fluxo sem 404 aleatórios, suba com as duas probabilidades em `0`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Disponível em `http://localhost:5173`; o dev server faz proxy de `/api`
para `http://localhost:8080` (ajuste com `VITE_API_TARGET`).

### Testes

```bash
cd backend
./mvnw test
```

Testes unitários e de web slice não precisam de infra; o teste de
integração ([`VotacaoIntegrationTest`](backend/src/test/java/com/desafio/votacao/VotacaoIntegrationTest.java))
sobe um PostgreSQL via Testcontainers e **requer Docker**.

### Teste de carga (Bônus 2)

Ver [perf/README.md](perf/README.md).

## API

Base: `/api/v1`. Referência com exemplos em [docs/API.md](docs/API.md);
arquivo pronto para o REST Client em [backend/requests.http](backend/requests.http).

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/pautas` | Cadastra uma pauta |
| `GET` | `/pautas` | Lista as pautas |
| `POST` | `/pautas/{id}/sessao` | Abre a sessão (corpo opcional; default 1 min) |
| `GET` | `/pautas/{id}/sessao` | Consulta a sessão |
| `POST` | `/pautas/{id}/votos` | Registra um voto (`SIM`/`NAO`) |
| `GET` | `/pautas/{id}/resultado` | Apura o resultado |

## Decisões de projeto

- **Arquitetura em camadas simples**: `web` (controllers + DTOs),
  `service` (regra de negócio, transações), `repository` (Spring Data),
  `domain` (entidades). Sem módulos nem abstrações que o tamanho do
  problema não pede.
- **Persistência**: PostgreSQL + Flyway. As regras que não podem ser
  violadas nem sob concorrência ficam no banco: `sessao_votacao.pauta_id`
  único (uma sessão por pauta) e `unique (sessao_id, associado_id)` (um
  voto por associado). O `VotoService` ainda traduz a violação de
  unicidade em `409` para o caso de corrida.
- **Sessão sem job de fechamento**: a sessão guarda `abertura` e
  `encerramento`; estar aberta é `agora ∈ [abertura, encerramento)`,
  calculado na leitura. Menos peças móveis, nada de scheduler para manter.
- **Apuração no banco**: `count` por escolha em vez de carregar votos;
  escala para o cenário do Bônus 2.
- **Elegibilidade (Bônus 1)**: `AssociadoElegibilidadeClient` com
  implementação fake atrás da interface. CPF malformado ou "não
  encontrado" e `UNABLE_TO_VOTE` viram `404`. As probabilidades são
  configuráveis para não travar avaliação manual.
- **Erros**: um único `@RestControllerAdvice` converte exceções em um
  corpo `ApiError` consistente (`404`/`409`/`400`/`500`).
- **Versionamento (Bônus 3)**: prefixo `/api/v1`; racional completo em
  [docs/versionamento-api.md](docs/versionamento-api.md).
- **Logs**: SLF4J nos serviços, `INFO` nas operações de escrita (pauta
  criada, sessão aberta, voto registrado, elegibilidade consultada) e
  `ERROR` com stack trace no handler de erro inesperado.

## Estrutura

```
backend/    API Spring Boot (Maven wrapper incluso)
frontend/   SPA React + Vite
perf/       teste de carga k6 (Bônus 2)
docs/       referência da API e estratégia de versionamento
docker-compose.yml   PostgreSQL
```
