import { useEffect, useState } from "react";
import { getCustomers, getHistory } from "../api";

export default function TransactionHistory() {
  const [customers, setCustomers] = useState([]);
  const [account, setAccount] = useState("");
  const [rows, setRows] = useState([]);

  useEffect(() => { getCustomers().then((r) => setCustomers(r.data)).catch(() => {}); }, []);

  useEffect(() => {
    if (!account) { setRows([]); return; }
    getHistory(account).then((r) => setRows(r.data)).catch(() => setRows([]));
  }, [account]);

  return (
    <div>
      <h2>Histórico de transacciones</h2>
      <select value={account} onChange={(e) => setAccount(e.target.value)}>
        <option value="">Selecciona un cliente</option>
        {customers.map((c) => (
          <option key={c.id} value={c.accountNumber}>
            {c.firstName} {c.lastName} ({c.accountNumber})
          </option>
        ))}
      </select>

      {account && (
        <table>
          <thead>
            <tr><th>ID</th><th>Fecha</th><th>Origen</th><th>Destino</th><th>Monto</th><th>Tipo</th></tr>
          </thead>
          <tbody>
            {rows.length === 0 && <tr><td colSpan="6">Sin transacciones</td></tr>}
            {rows.map((t) => (
              <tr key={t.id}>
                <td>{t.id}</td>
                <td>{t.timestamp ? new Date(t.timestamp).toLocaleString() : "-"}</td>
                <td>{t.senderAccountNumber}</td>
                <td>{t.receiverAccountNumber}</td>
                <td>${Number(t.amount).toFixed(2)}</td>
                <td>{t.senderAccountNumber === account ? "Enviada" : "Recibida"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
