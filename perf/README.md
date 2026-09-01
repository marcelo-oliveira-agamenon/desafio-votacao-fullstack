# Testes de performance (Tarefa Bônus 2)

Teste de carga com [k6](https://k6.io/) para o cenário de "centenas de
milhares de votos". O script cria algumas pautas com sessão aberta e então
dispara votos concorrentes, cada um com um CPF válido gerado na hora.

## Pré-requisitos

- k6 instalado (`brew install k6`, `choco install k6` ou binário do site)
- Backend no ar (`./mvnw spring-boot:run` na pasta `backend`)
- Para evitar 404 aleatórios do client de elegibilidade, suba o backend com
  o sorteio desligado:

  ```
  ELEGIBILIDADE_PROB_CPF_INVALIDO=0 ELEGIBILIDADE_PROB_INELEGIVEL=0 ./mvnw spring-boot:run
  ```

## Execução

```
k6 run perf/votacao-load.js
```

### Parâmetros (variáveis de ambiente)

| Var | Padrão | Descrição |
| --- | --- | --- |
| `BASE_URL` | `http://localhost:8080` | URL do backend |
| `VUS` | `200` | usuários virtuais simultâneos no pico |
| `DURATION` | `2m` | tempo no pico (fora as rampas) |
| `PAUTAS` | `10` | pautas criadas no setup, sorteadas pelos votos |

Exemplo com carga maior:

```
VUS=500 DURATION=5m k6 run perf/votacao-load.js
```

## Critérios (thresholds)

O teste falha se:

- taxa de erro HTTP >= 1% (`http_req_failed`)
- p95 de latência >= 500 ms (`http_req_duration`)
