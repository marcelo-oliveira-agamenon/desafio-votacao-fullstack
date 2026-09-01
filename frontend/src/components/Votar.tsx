import { useState } from "react";
import { api, RequestError } from "../api/client";
import type { Escolha } from "../api/types";
import { Alert, Button, Field, inputClass } from "./ui";

export function Votar({ pautaId, onVotou }: { pautaId: number; onVotou: () => void }) {
  const [associadoId, setAssociadoId] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [mensagem, setMensagem] = useState<string | null>(null);
  const [enviando, setEnviando] = useState<Escolha | null>(null);

  async function votar(escolha: Escolha) {
    const cpf = associadoId.trim();
    if (cpf === "") return;
    setErro(null);
    setMensagem(null);
    setEnviando(escolha);
    try {
      await api.registrarVoto(pautaId, cpf, escolha);
      setMensagem(`Voto "${escolha}" registrado.`);
      setAssociadoId("");
      onVotou();
    } catch (falha) {
      setErro(falha instanceof RequestError ? falha.message : "Falha ao registrar o voto");
    } finally {
      setEnviando(null);
    }
  }

  const bloqueado = enviando !== null || associadoId.trim() === "";

  return (
    <div className="space-y-2">
      <Field label="CPF do associado">
        <input
          className={inputClass}
          value={associadoId}
          onChange={(e) => setAssociadoId(e.target.value)}
          placeholder="000.000.000-00"
        />
      </Field>
      <div className="flex gap-2">
        <Button variant="sim" disabled={bloqueado} onClick={() => votar("SIM")}>
          Sim
        </Button>
        <Button variant="nao" disabled={bloqueado} onClick={() => votar("NAO")}>
          Não
        </Button>
      </div>
      {mensagem && <p className="text-sm text-emerald-700">{mensagem}</p>}
      {erro && <Alert>{erro}</Alert>}
    </div>
  );
}
