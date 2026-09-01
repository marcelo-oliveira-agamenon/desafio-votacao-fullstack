export function formatarDataHora(iso: string): string {
  return new Date(iso).toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
}

export function segundosRestantes(encerramentoIso: string, agora: number = Date.now()): number {
  return Math.max(0, Math.round((new Date(encerramentoIso).getTime() - agora) / 1000));
}

export function formatarContagem(segundos: number): string {
  const min = Math.floor(segundos / 60);
  const seg = segundos % 60;
  return `${min}:${String(seg).padStart(2, "0")}`;
}
