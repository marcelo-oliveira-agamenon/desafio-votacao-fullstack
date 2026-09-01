import { useState, type FormEvent } from "react";
import { api, RequestError } from "../api/client";
import { Alert, Button, Field, inputClass } from "./ui";

export function CriarPautaForm({ onCriada }: { onCriada: () => void }) {
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  async function submeter(evento: FormEvent) {
    evento.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await api.criarPauta(titulo.trim(), descricao.trim());
      setTitulo("");
      setDescricao("");
      onCriada();
    } catch (falha) {
      setErro(falha instanceof RequestError ? falha.message : "Falha ao criar a pauta");
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form onSubmit={submeter} className="space-y-3">
      <Field label="Título">
        <input
          className={inputClass}
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          maxLength={200}
          required
        />
      </Field>
      <Field label="Descrição (opcional)">
        <textarea
          className={inputClass}
          value={descricao}
          onChange={(e) => setDescricao(e.target.value)}
          maxLength={2000}
          rows={2}
        />
      </Field>
      {erro && <Alert>{erro}</Alert>}
      <Button type="submit" disabled={enviando || titulo.trim() === ""}>
        {enviando ? "Cadastrando..." : "Cadastrar pauta"}
      </Button>
    </form>
  );
}
