import type { Resultado } from "../api/types";

const ROTULO: Record<Resultado["desfecho"], string> = {
  APROVADA: "Aprovada",
  REJEITADA: "Rejeitada",
  EMPATE: "Empate",
};

export function ResultadoView({ resultado }: { resultado: Resultado }) {
  const { votosSim, votosNao, totalVotos, desfecho, sessaoEncerrada } = resultado;
  const pctSim = totalVotos === 0 ? 0 : Math.round((votosSim / totalVotos) * 100);

  return (
    <div className="space-y-2">
      <div className="flex items-center justify-between text-sm">
        <span className="font-medium">{sessaoEncerrada ? "Resultado final" : "Parcial"}</span>
        <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-semibold text-slate-700">
          {ROTULO[desfecho]}
        </span>
      </div>
      <div className="flex h-2 overflow-hidden rounded-full bg-rose-200">
        <div className="bg-emerald-500" style={{ width: `${pctSim}%` }} />
      </div>
      <div className="flex justify-between text-xs text-slate-600">
        <span>Sim: {votosSim}</span>
        <span>Não: {votosNao}</span>
        <span>Total: {totalVotos}</span>
      </div>
    </div>
  );
}
