# Frontend — Votação

SPA em React 19 + Vite + TypeScript + Tailwind CSS. Consome a API em
`/api/v1` (o dev server faz proxy para o backend).

```bash
npm install
npm run dev      # http://localhost:5173
npm run build    # gera dist/
npm run preview  # serve o build
```

`VITE_API_TARGET` muda o alvo do proxy (default `http://localhost:8080`).

## Organização

```
src/
  api/         client HTTP tipado e tipos dos contratos
  components/   formulário de pauta, item de pauta, votação, resultado, UI base
  lib/          formatação de data e contagem regressiva
  App.tsx       lista de pautas + cadastro
```
