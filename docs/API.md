# API

Base URL: `http://localhost:8080/api/v1`

Todas as respostas são JSON. Datas em ISO-8601 UTC.

## Endpoints

| Método | Rota | Descrição | Sucesso |
| --- | --- | --- | --- |
| `POST` | `/pautas` | Cadastra uma pauta | `201 Created` + `Location` |
| `GET` | `/pautas` | Lista as pautas (mais recentes primeiro) | `200 OK` |
| `POST` | `/pautas/{id}/sessao` | Abre a sessão de votação da pauta | `201 Created` |
| `GET` | `/pautas/{id}/sessao` | Consulta a sessão da pauta | `200 OK` |
| `POST` | `/pautas/{id}/votos` | Registra o voto de um associado | `201 Created` |
| `GET` | `/pautas/{id}/resultado` | Apura os votos da pauta | `200 OK` |

## Exemplos

### Cadastrar pauta

```bash
curl -i -X POST http://localhost:8080/api/v1/pautas \
  -H 'Content-Type: application/json' \
  -d '{"titulo": "Reforma do estatuto", "descricao": "Votação da nova redação"}'
```

```json
{ "id": 1, "titulo": "Reforma do estatuto", "descricao": "Votação da nova redação", "criadaEm": "2026-09-01T12:00:00Z" }
```

### Abrir sessão

Corpo opcional. Sem corpo, a sessão dura 1 minuto; `duracaoSegundos` define
outra duração (máximo 86400).

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessao \
  -H 'Content-Type: application/json' \
  -d '{"duracaoSegundos": 300}'
```

```json
{ "id": 1, "pautaId": 1, "abertura": "2026-09-01T12:01:00Z", "encerramento": "2026-09-01T12:06:00Z", "aberta": true }
```

### Registrar voto

`escolha` é `SIM` ou `NAO`. `associadoId` é o CPF do associado (Tarefa Bônus 1).

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/votos \
  -H 'Content-Type: application/json' \
  -d '{"associadoId": "111.444.777-35", "escolha": "SIM"}'
```

```json
{ "id": 10, "pautaId": 1, "associadoId": "11144477735", "escolha": "SIM", "registradoEm": "2026-09-01T12:02:00Z" }
```

### Apurar resultado

```bash
curl http://localhost:8080/api/v1/pautas/1/resultado
```

```json
{ "pautaId": 1, "sessaoEncerrada": true, "votosSim": 7, "votosNao": 3, "totalVotos": 10, "desfecho": "APROVADA" }
```

`desfecho`: `APROVADA` (sim > não), `REJEITADA` (não > sim) ou `EMPATE`.

## Erros

Formato único para toda falha tratada:

```json
{
  "timestamp": "2026-09-01T12:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "O associado 11144477735 já votou nesta pauta",
  "path": "/api/v1/pautas/1/votos",
  "fieldErrors": []
}
```

| Status | Quando |
| --- | --- |
| `400 Bad Request` | corpo inválido (`fieldErrors` preenchido) ou JSON malformado |
| `404 Not Found` | pauta/sessão inexistente; CPF não reconhecido ou `UNABLE_TO_VOTE` (Bônus 1) |
| `409 Conflict` | sessão já aberta, sessão fechada ou associado já votou |
| `500 Internal Server Error` | erro não previsto (registrado em log) |
