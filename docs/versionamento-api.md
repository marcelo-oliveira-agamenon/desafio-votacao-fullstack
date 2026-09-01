# Versionamento da API (Tarefa Bônus 3)

## Estratégia adotada

Versionamento por **URI**: todo endpoint fica sob `/api/v{n}` — hoje `/api/v1`.

```
POST /api/v1/pautas
POST /api/v1/pautas/{id}/sessao
POST /api/v1/pautas/{id}/votos
GET  /api/v1/pautas/{id}/resultado
```

## Por que URI e não as alternativas

| Estratégia | Problema para este caso |
| --- | --- |
| Header custom (`X-API-Version`) | Invisível em log/curl/browser; fácil de esquecer |
| `Accept: application/vnd.votacao.v2+json` | Correto em teoria, mas verboso e difícil de testar manualmente |
| Query param (`?version=2`) | Polui cache e métricas; mistura versão com filtro de recurso |

A URI é explícita, aparece em log e em qualquer client, é trivial de rotear
(gateway, load balancer) e de observar por versão. Para uma API pequena como
esta, é o melhor custo/benefício.

## Regras de evolução

- **Mudança retrocompatível** (campo novo opcional, novo endpoint, header novo):
  permanece em `v1`, sem bump.
- **Breaking change** (remover/renomear campo, mudar tipo, mudar semântica de
  status): entra em `v2`. `v1` continua respondendo.
- **Janela de suporte**: mantém-se a versão anterior (N-1) no ar.
- **Depreciação**: respostas da versão antiga passam a enviar os headers
  `Deprecation: true` e `Sunset: <data>`; a remoção só ocorre após essa data.

## Como uma v2 conviveria no código

Os controllers ficam em `web` e os contratos em `web.dto`. Uma v2 seria um
pacote `web.v2` / `web.dto.v2` com seus próprios `@RequestMapping("/api/v2/...")`,
reaproveitando a camada de `service` (regra de negócio não é versionada). Assim
as duas versões coexistem sem `if (version == ...)` espalhado pelo código.
