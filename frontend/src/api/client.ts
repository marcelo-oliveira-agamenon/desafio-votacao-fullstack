import type { ApiError, Escolha, Pauta, Resultado, Sessao, Voto } from "./types";

const BASE = "/api/v1";

export class RequestError extends Error {
  readonly status: number;
  readonly fieldErrors: { field: string; message: string }[];

  constructor(status: number, message: string, fieldErrors: { field: string; message: string }[] = []) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(BASE + path, {
    ...init,
    headers: { "Content-Type": "application/json", ...init?.headers },
  });

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  const body = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const error = body as ApiError | null;
    throw new RequestError(
      response.status,
      error?.message ?? `Erro ${response.status}`,
      error?.fieldErrors ?? [],
    );
  }
  return body as T;
}

export const api = {
  listarPautas: () => request<Pauta[]>("/pautas"),

  criarPauta: (titulo: string, descricao: string) =>
    request<Pauta>("/pautas", {
      method: "POST",
      body: JSON.stringify({ titulo, descricao: descricao || null }),
    }),

  abrirSessao: (pautaId: number, duracaoSegundos?: number) =>
    request<Sessao>(`/pautas/${pautaId}/sessao`, {
      method: "POST",
      body: JSON.stringify(duracaoSegundos ? { duracaoSegundos } : {}),
    }),

  consultarSessao: (pautaId: number) => optional(request<Sessao>(`/pautas/${pautaId}/sessao`)),

  registrarVoto: (pautaId: number, associadoId: string, escolha: Escolha) =>
    request<Voto>(`/pautas/${pautaId}/votos`, {
      method: "POST",
      body: JSON.stringify({ associadoId, escolha }),
    }),

  consultarResultado: (pautaId: number) =>
    optional(request<Resultado>(`/pautas/${pautaId}/resultado`)),
};

// 404 nesses recursos significa "ainda não existe", não um erro de fato.
async function optional<T>(promise: Promise<T>): Promise<T | null> {
  try {
    return await promise;
  } catch (error) {
    if (error instanceof RequestError && error.status === 404) {
      return null;
    }
    throw error;
  }
}
