package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.DTO.TransferRequestDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.entity.Transaction;
import com.udea.bancoudea.mapper.TransactionMapperImpl;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CustomerRepository customerRepository;

    private TransactionService service;
    private Customer ana;
    private Customer luis;

    @BeforeEach
    void setUp() {
        service = new TransactionService(transactionRepository, customerRepository, new TransactionMapperImpl());
        ana = new Customer(1L, "111", "Ana", "Ruiz", 1000.0);
        luis = new Customer(2L, "222", "Luis", "Perez", 500.0);
    }

    private TransferRequestDTO req(String from, String to, Double amount) {
        TransferRequestDTO r = new TransferRequestDTO();
        r.setSenderAccountNumber(from);
        r.setReceiverAccountNumber(to);
        r.setAmount(amount);
        return r;
    }

    @Test
    void transferMoney_ok_actualizaSaldosYGuardaTransaccion() {
        when(customerRepository.findByAccountNumber("111")).thenReturn(Optional.of(ana));
        when(customerRepository.findByAccountNumber("222")).thenReturn(Optional.of(luis));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TransactionDTO result = service.transferMoney(req("111", "222", 100.0));

        assertEquals(900.0, ana.getBalance());
        assertEquals(600.0, luis.getBalance());
        assertEquals(10L, result.getId());
        assertEquals(100.0, result.getAmount());
        assertNotNull(result.getTimestamp());
        verify(customerRepository, times(2)).save(any(Customer.class));
    }

    @Test
    void transferMoney_saldoInsuficiente() {
        when(customerRepository.findByAccountNumber("111")).thenReturn(Optional.of(ana));
        when(customerRepository.findByAccountNumber("222")).thenReturn(Optional.of(luis));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transferMoney(req("111", "222", 5000.0)));

        assertEquals("Saldo insuficiente", ex.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferMoney_mismaCuenta() {
        assertThrows(IllegalArgumentException.class, () -> service.transferMoney(req("111", "111", 10.0)));
    }

    @Test
    void transferMoney_montoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> service.transferMoney(req("111", "222", 0.0)));
        assertThrows(IllegalArgumentException.class, () -> service.transferMoney(req("111", "222", null)));
    }

    @Test
    void transferMoney_cuentasNulas() {
        assertThrows(IllegalArgumentException.class, () -> service.transferMoney(req(null, "222", 10.0)));
        assertThrows(IllegalArgumentException.class, () -> service.transferMoney(req("111", null, 10.0)));
    }

    @Test
    void transferMoney_cuentaOrigenNoExiste() {
        when(customerRepository.findByAccountNumber("999")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transferMoney(req("999", "222", 10.0)));
        assertEquals("Cuenta origen no encontrada", ex.getMessage());
    }

    @Test
    void transferMoney_cuentaDestinoNoExiste() {
        when(customerRepository.findByAccountNumber("111")).thenReturn(Optional.of(ana));
        when(customerRepository.findByAccountNumber("999")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.transferMoney(req("111", "999", 10.0)));
        assertEquals("Cuenta destino no encontrada", ex.getMessage());
    }

    @Test
    void getTransactionsForAccount_devuelveHistorico() {
        Transaction t = new Transaction();
        t.setId(1L);
        t.setSenderAccountNumber("111");
        t.setReceiverAccountNumber("222");
        t.setAmount(50.0);
        t.setTimestamp(LocalDateTime.now());
        when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumberOrderByTimestampDesc("111", "111"))
                .thenReturn(List.of(t));

        List<TransactionDTO> result = service.getTransactionsForAccount("111");

        assertEquals(1, result.size());
        assertEquals(50.0, result.get(0).getAmount());
    }

    @Test
    void getAll_y_getById() {
        Transaction t = new Transaction();
        t.setId(5L);
        t.setSenderAccountNumber("111");
        t.setReceiverAccountNumber("222");
        t.setAmount(10.0);
        t.setTimestamp(LocalDateTime.now());
        when(transactionRepository.findAll()).thenReturn(List.of(t));
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(t));
        when(transactionRepository.findById(6L)).thenReturn(Optional.empty());

        assertEquals(1, service.getAll().size());
        assertEquals(5L, service.getById(5L).getId());
        assertThrows(java.util.NoSuchElementException.class, () -> service.getById(6L));
    }
}
