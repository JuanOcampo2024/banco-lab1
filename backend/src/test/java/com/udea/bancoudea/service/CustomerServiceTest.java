package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.mapper.CustomerMapperImpl;
import com.udea.bancoudea.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    private CustomerService service;

    @BeforeEach
    void setUp() {
        service = new CustomerService(customerRepository, new CustomerMapperImpl());
    }

    private CustomerDTO dto(String acc, Double balance) {
        return new CustomerDTO(null, "Ana", "Ruiz", acc, balance);
    }

    @Test
    void createCustomer_ok() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });
        CustomerDTO result = service.createCustomer(dto("111", 100.0));
        assertEquals(1L, result.getId());
        assertEquals("111", result.getAccountNumber());
    }

    @Test
    void createCustomer_saldoNuloONegativo() {
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(dto("111", null)));
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(dto("111", -5.0)));
    }

    @Test
    void createCustomer_cuentaVacia() {
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(dto(" ", 10.0)));
        assertThrows(IllegalArgumentException.class, () -> service.createCustomer(dto(null, 10.0)));
    }

    @Test
    void getAllCustomer_y_getById() {
        Customer c = new Customer(1L, "111", "Ana", "Ruiz", 10.0);
        when(customerRepository.findAll()).thenReturn(List.of(c));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        when(customerRepository.findById(2L)).thenReturn(Optional.empty());

        assertEquals(1, service.getAllCustomer().size());
        assertEquals("Ana", service.getCustomerById(1L).getFirstName());
        assertThrows(NoSuchElementException.class, () -> service.getCustomerById(2L));
    }

    @Test
    void updateCustomer_actualizaCampos() {
        Customer c = new Customer(1L, "111", "Ana", "Ruiz", 10.0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerDTO changes = new CustomerDTO(null, "Maria", "Lopez", "999", 77.0);
        CustomerDTO result = service.updateCustomer(1L, changes);

        assertEquals("Maria", result.getFirstName());
        assertEquals("Lopez", result.getLastName());
        assertEquals("999", result.getAccountNumber());
        assertEquals(77.0, result.getBalance());
    }

    @Test
    void updateCustomer_noExiste() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.updateCustomer(9L, dto("1", 1.0)));
    }

    @Test
    void deleteCustomer_ok_y_noExiste() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.existsById(2L)).thenReturn(false);

        service.deleteCustomer(1L);
        verify(customerRepository).deleteById(1L);
        assertThrows(NoSuchElementException.class, () -> service.deleteCustomer(2L));
    }
}
