import { useCallback, useEffect, useState } from "react";
import { api, RequestError } from "../api/client";
import type { Pauta, Resultado, Sessao } from "../api/types";
import { formatarContagem, formatarDataHora, segundosRestantes } from "../lib/format";
import { Alert, Button, Card } from "./ui";
import { ResultadoView } from "./Resultado";
import { Votar } from "./Votar";

export function PautaItem({ pauta }: { pauta: Pauta }) {
  const [sessao, setSessao] = useState<Sessao | null>(null);
  const [resultado, setResultado] = useState<Resultado | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [abrindo, setAbrindo] = useState(false);
  const [minutos, setMinutos] = useState(1);
  const [agora, setAgora] = useState(Date.now());

  const recarregar = useCallback(async () => {
    try {
      const atual = await api.consultarSessao(pauta.id);
      setSessao(atual);
      setResultado(atual ? await api.consultarResultado(pauta.id) : null);
      setErro(null);
    } catch (falha) {
      setErro(falha instanceof RequestError ? falha.message : "Falha ao carregar a pauta");
    } finally {
      setCarregando(false);
    }
  }, [pauta.id]);

  useEffect(() => {
    void recarregar();
  }, [recarregar]);

  // Relógio de 1s enquanto a sessão está aberta (contagem regressiva).
  useEffect(() => {
    if (!sessao?.aberta) return;
    const id = window.setInterval(() => setAgora(Date.now()), 1000);
    return () => window.clearInterval(id);
  }, [sessao?.aberta]);

  const restante = sessao ? segundosRestantes(sessao.encerramento, agora) : 0;

  // Atualiza a parcial a cada 3s; quando zera o tempo, recarrega para fechar.
  useEffect(() => {
    if (!sessao?.aberta) return;
    if (restante === 0) {
      void recarregar();
      return;
    }
    const id = window.setInterval(() => {
      api.consultarResultado(pauta.id).then(setResultado).catch(() => undefined);
    }, 3000);
    return () => window.clearInterval(id);
  }, [sessao?.aberta, restante, pauta.id, recarregar]);

  async function abrirSessao() {
    setErro(null);
    setAbrindo(true);
    try {
      setSessao(await api.abrirSessao(pauta.id, minutos * 60));
      setResultado(await api.consultarResultado(pauta.id));
    } catch (falha) {
      setErro(falha instanceof RequestError ? falha.message : "Falha ao abrir a sessão");
    } finally {
      setAbrindo(false);
    }
  }

  return (
    <Card>
      <div className="space-y-3">
        <div>
          <h3 className="font-semibold">{pauta.titulo}</h3>
          {pauta.descricao && <p className="mt-0.5 text-sm text-slate-600">{pauta.descricao}</p>}
          <p className="mt-1 text-xs text-slate-400">Criada em {formatarDataHora(pauta.criadaEm)}</p>
        </div>

        {erro && <Alert>{erro}</Alert>}

        {carregando ? (
          <p className="text-sm text-slate-400">Carregando...</p>
        ) : !sessao ? (
          <div className="flex flex-wrap items-end gap-2 border-t border-slate-100 pt-3">
            <label className="text-sm">
              <span className="mb-1 block font-medium text-slate-700">Duração (min)</span>
              <input
                type="number"
                min={1}
                value={minutos}
                onChange={(e) => setMinutos(Math.max(1, Number(e.target.value)))}
                className="w-24 rounded-md border border-slate-300 px-3 py-2 text-sm"
              />
            </label>
            <Button onClick={abrirSessao} disabled={abrindo}>
              {abrindo ? "Abrindo..." : "Abrir sessão"}
            </Button>
          </div>
        ) : (
          <div className="space-y-3 border-t border-slate-100 pt-3">
            {sessao.aberta ? (
              <>
                <p className="text-sm font-medium text-emerald-700">
                  Sessão aberta — encerra em {formatarContagem(restante)}
                </p>
                <Votar pautaId={pauta.id} onVotou={recarregar} />
              </>
            ) : (
              <p className="text-sm text-slate-500">
                Sessão encerrada em {formatarDataHora(sessao.encerramento)}
              </p>
            )}
            {resultado && <ResultadoView resultado={resultado} />}
          </div>
        )}
      </div>
    </Card>
  );
}
