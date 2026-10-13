import { useEffect, useState } from "react";
import { getCustomers, transfer } from "../api";

const empty = { senderAccountNumber: "", receiverAccountNumber: "", amount: "" };

export default function TransferForm() {
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState(empty);
  const [msg, setMsg] = useState({ type: "", text: "" });

  const load = () => getCustomers().then((r) => setCustomers(r.data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await transfer({ ...form, amount: parseFloat(form.amount) });
      setMsg({ type: "ok", text: "Transferencia realizada con éxito" });
      setForm(empty);
      load();
    } catch (err) {
      setMsg({ type: "error", text: err.response?.data?.message || "Error en la transferencia" });
    }
  };

  return (
    <div>
      <h2>Transferir dinero</h2>
      <form onSubmit={handleSubmit} className="col">
        <select name="senderAccountNumber" value={form.senderAccountNumber} onChange={handleChange} required>
          <option value="">Cuenta origen</option>
          {customers.map((c) => (
            <option key={c.id} value={c.accountNumber}>
              {c.accountNumber} - {c.firstName} {c.lastName} (${Number(c.balance).toFixed(2)})
            </option>
          ))}
        </select>
        <select name="receiverAccountNumber" value={form.receiverAccountNumber} onChange={handleChange} required>
          <option value="">Cuenta destino</option>
          {customers.map((c) => (
            <option key={c.id} value={c.accountNumber}>
              {c.accountNumber} - {c.firstName} {c.lastName}
            </option>
          ))}
        </select>
        <input name="amount" type="number" step="0.01" min="0.01" placeholder="Monto" value={form.amount} onChange={handleChange} required />
        <button type="submit">Transferir</button>
      </form>
      {msg.text && <p className={msg.type}>{msg.text}</p>}
    </div>
  );
}
