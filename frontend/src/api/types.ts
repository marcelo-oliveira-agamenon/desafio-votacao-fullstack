export type Escolha = "SIM" | "NAO";
export type Desfecho = "APROVADA" | "REJEITADA" | "EMPATE";

export interface Pauta {
  id: number;
  titulo: string;
  descricao: string | null;
  criadaEm: string;
}

export interface Sessao {
  id: number;
  pautaId: number;
  abertura: string;
  encerramento: string;
  aberta: boolean;
}

export interface Voto {
  id: number;
  pautaId: number;
  associadoId: string;
  escolha: Escolha;
  registradoEm: string;
}

export interface Resultado {
  pautaId: number;
  sessaoEncerrada: boolean;
  votosSim: number;
  votosNao: number;
  totalVotos: number;
  desfecho: Desfecho;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: { field: string; message: string }[];
}
