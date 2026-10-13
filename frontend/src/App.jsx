import { BrowserRouter, Routes, Route, NavLink } from "react-router-dom";
import CustomerList from "./components/CustomerList";
import TransferForm from "./components/TransferForm";
import TransactionHistory from "./components/TransactionHistory";
import "./App.css";

export default function App() {
  return (
    <BrowserRouter>
      <nav>
        <strong>🏦 Banco UdeA</strong>
        <NavLink to="/" end>Clientes</NavLink>
        <NavLink to="/transferir">Transferir</NavLink>
        <NavLink to="/historico">Histórico</NavLink>
      </nav>
      <main>
        <Routes>
          <Route path="/" element={<CustomerList />} />
          <Route path="/transferir" element={<TransferForm />} />
          <Route path="/historico" element={<TransactionHistory />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}
