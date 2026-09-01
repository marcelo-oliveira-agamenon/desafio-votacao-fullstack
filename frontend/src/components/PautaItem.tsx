import type { Pauta } from "../api/types";
import { formatarDataHora } from "../lib/format";
import { Card } from "./ui";

export function PautaItem({ pauta }: { pauta: Pauta }) {
  return (
    <Card>
      <h3 className="font-semibold">{pauta.titulo}</h3>
      {pauta.descricao && <p className="mt-0.5 text-sm text-slate-600">{pauta.descricao}</p>}
      <p className="mt-1 text-xs text-slate-400">Criada em {formatarDataHora(pauta.criadaEm)}</p>
    </Card>
  );
}
