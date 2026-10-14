package com.udea.bancoudea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BancoIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private void crearCliente(String cuenta, double saldo, int esperado) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "accountNumber", cuenta, "firstName", "Test", "lastName", "User", "balance", saldo));
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is(esperado));
    }

    private String transferencia(String de, String para, double monto) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "senderAccountNumber", de, "receiverAccountNumber", para, "amount", monto));
    }

    @Test
    void flujoCompleto_crearClientes_transferir_y_consultarHistorico() throws Exception {
        crearCliente("INT-A", 1000.0, 200);
        crearCliente("INT-B", 500.0, 200);

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(transferencia("INT-A", "INT-B", 100.0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.timestamp").exists());

        mockMvc.perform(get("/api/transactions/account/INT-A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].receiverAccountNumber").value("INT-B"));

        mockMvc.perform(get("/api/transactions")).andExpect(status().isOk());
        mockMvc.perform(get("/api/customers")).andExpect(status().isOk());
    }

    @Test
    void transferirSinSaldo_devuelve400ConMensaje() throws Exception {
        crearCliente("INT-C", 10.0, 200);
        crearCliente("INT-D", 10.0, 200);

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content(transferencia("INT-C", "INT-D", 999.0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo insuficiente"));
    }

    @Test
    void cuentaDuplicada_devuelve409() throws Exception {
        crearCliente("INT-E", 10.0, 200);
        crearCliente("INT-E", 10.0, 409);
    }

    @Test
    void clienteInexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/customers/999999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/transactions/999999")).andExpect(status().isNotFound());
    }

    @Test
    void actualizarYBorrarCliente() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "accountNumber", "INT-F", "firstName", "Old", "lastName", "Name", "balance", 5.0));
        String created = mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(put("/api/customers/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("firstName", "New"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"));

        mockMvc.perform(get("/api/customers/" + id)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/customers/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/customers/" + id)).andExpect(status().isNotFound());
    }
}
