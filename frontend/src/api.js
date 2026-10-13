import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080/api" });

export const getCustomers = () => api.get("/customers");
export const createCustomer = (c) => api.post("/customers", c);
export const updateCustomer = (id, c) => api.put(`/customers/${id}`, c);
export const deleteCustomer = (id) => api.delete(`/customers/${id}`);
export const transfer = (t) => api.post("/transactions", t);
export const getHistory = (acc) => api.get(`/transactions/account/${acc}`);
