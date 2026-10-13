import { useEffect, useState } from "react";
import { getCustomers, createCustomer, updateCustomer, deleteCustomer } from "../api";

const empty = { accountNumber: "", firstName: "", lastName: "", balance: "" };

export default function CustomerList() {
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  const load = () =>
    getCustomers()
      .then((r) => setCustomers(r.data))
      .catch(() => setError("No se pudo conectar con el backend (¿está corriendo en :8080?)"));

  useEffect(() => { load(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    const payload = { ...form, balance: parseFloat(form.balance) };
    try {
      if (editingId) await updateCustomer(editingId, payload);
      else await createCustomer(payload);
      setForm(empty);
      setEditingId(null);
      load();
    } catch (err) {
      setError(err.response?.data?.message || "Error al guardar el cliente");
    }
  };

  const startEdit = (c) => {
    setEditingId(c.id);
    setForm({ accountNumber: c.accountNumber, firstName: c.firstName, lastName: c.lastName, balance: c.balance });
  };

  const cancelEdit = () => { setEditingId(null); setForm(empty); };

  const handleDelete = async (c) => {
    if (!window.confirm(`¿Eliminar a ${c.firstName} ${c.lastName}?`)) return;
    try { await deleteCustomer(c.id); load(); }
    catch (err) { setError(err.response?.data?.message || "No se pudo eliminar"); }
  };

  return (
    <div>
      <h2>Clientes</h2>
      <form onSubmit={handleSubmit} className="row">
        <input name="accountNumber" placeholder="N° cuenta" value={form.accountNumber} onChange={handleChange} required />
        <input name="firstName" placeholder="Nombre" value={form.firstName} onChange={handleChange} required />
        <input name="lastName" placeholder="Apellido" value={form.lastName} onChange={handleChange} required />
        <input name="balance" type="number" step="0.01" min="0" placeholder="Saldo" value={form.balance} onChange={handleChange} required />
        <button type="submit">{editingId ? "Guardar cambios" : "Crear cliente"}</button>
        {editingId && <button type="button" className="sec" onClick={cancelEdit}>Cancelar</button>}
      </form>
      {error && <p className="error">{error}</p>}

      <table>
        <thead>
          <tr><th>ID</th><th>Cuenta</th><th>Nombre</th><th>Apellido</th><th>Saldo</th><th>Acciones</th></tr>
        </thead>
        <tbody>
          {customers.length === 0 && <tr><td colSpan="6">Sin clientes</td></tr>}
          {customers.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td><td>{c.accountNumber}</td><td>{c.firstName}</td>
              <td>{c.lastName}</td><td>${Number(c.balance).toFixed(2)}</td>
              <td className="actions">
                <button onClick={() => startEdit(c)}>Editar</button>
                <button className="danger" onClick={() => handleDelete(c)}>Borrar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
