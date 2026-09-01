import { useCallback, useEffect, useState } from "react";
import { api, RequestError } from "./api/client";
import type { Pauta } from "./api/types";
import { CriarPautaForm } from "./components/CriarPautaForm";
import { PautaItem } from "./components/PautaItem";
import { Alert, Card } from "./components/ui";

export default function App() {
  const [pautas, setPautas] = useState<Pauta[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      setPautas(await api.listarPautas());
      setErro(null);
    } catch (falha) {
      setErro(falha instanceof RequestError ? falha.message : "Falha ao carregar as pautas");
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto max-w-3xl px-4 py-4">
          <h1 className="text-xl font-semibold">Votação</h1>
          <p className="text-sm text-slate-500">Gestão de pautas e sessões de votação</p>
        </div>
      </header>

      <main className="mx-auto max-w-3xl space-y-6 px-4 py-8">
        <section className="space-y-3">
          <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">Nova pauta</h2>
          <Card>
            <CriarPautaForm onCriada={carregar} />
          </Card>
        </section>

        <section className="space-y-3">
          <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">Pautas</h2>
          {erro && <Alert>{erro}</Alert>}
          {carregando ? (
            <p className="text-sm text-slate-400">Carregando...</p>
          ) : pautas.length === 0 ? (
            <p className="text-sm text-slate-400">Nenhuma pauta cadastrada.</p>
          ) : (
            <div className="space-y-3">
              {pautas.map((pauta) => (
                <PautaItem key={pauta.id} pauta={pauta} />
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
