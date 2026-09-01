import http from "k6/http";
import { check } from "k6";

const BASE = __ENV.BASE_URL || "http://localhost:8080";
const API = `${BASE}/api/v1`;
const PAUTAS = Number(__ENV.PAUTAS || 10);
const VUS = Number(__ENV.VUS || 200);
const DURATION = __ENV.DURATION || "2m";
const JSON_HEADERS = { headers: { "Content-Type": "application/json" } };

export const options = {
  scenarios: {
    votos: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "30s", target: VUS },
        { duration: DURATION, target: VUS },
        { duration: "20s", target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<500"],
  },
};

function digitoVerificador(digitos, tamanho) {
  let soma = 0;
  let peso = tamanho + 1;
  for (let i = 0; i < tamanho; i++) {
    soma += Number(digitos[i]) * peso--;
  }
  const resto = soma % 11;
  return resto < 2 ? 0 : 11 - resto;
}

function gerarCpf() {
  let base = "";
  for (let i = 0; i < 9; i++) {
    base += Math.floor(Math.random() * 10);
  }
  const d1 = digitoVerificador(base, 9);
  const d2 = digitoVerificador(base + d1, 10);
  return `${base}${d1}${d2}`;
}

export function setup() {
  const ids = [];
  for (let i = 0; i < PAUTAS; i++) {
    const criada = http.post(
      `${API}/pautas`,
      JSON.stringify({ titulo: `Perf ${i} ${Date.now()}` }),
      JSON_HEADERS,
    );
    const pautaId = criada.json("id");
    http.post(
      `${API}/pautas/${pautaId}/sessao`,
      JSON.stringify({ duracaoSegundos: 3600 }),
      JSON_HEADERS,
    );
    ids.push(pautaId);
  }
  return { ids };
}

export default function (data) {
  const pautaId = data.ids[Math.floor(Math.random() * data.ids.length)];
  const body = JSON.stringify({
    associadoId: gerarCpf(),
    escolha: Math.random() < 0.5 ? "SIM" : "NAO",
  });
  const res = http.post(`${API}/pautas/${pautaId}/votos`, body, JSON_HEADERS);
  check(res, { "voto aceito (201)": (r) => r.status === 201 });
}
