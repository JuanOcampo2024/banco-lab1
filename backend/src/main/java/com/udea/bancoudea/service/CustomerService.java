package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.mapper.CustomerMapper;
import com.udea.bancoudea.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDTO> getAllCustomer() {
        return customerRepository.findAll().stream().map(customerMapper::toDTO).toList();
    }

    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id).map(customerMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado"));
    }

    public CustomerDTO createCustomer(CustomerDTO dto) {
        if (dto.getBalance() == null || dto.getBalance() < 0)
            throw new IllegalArgumentException("El saldo es obligatorio y no puede ser negativo");
        if (dto.getAccountNumber() == null || dto.getAccountNumber().isBlank())
            throw new IllegalArgumentException("El número de cuenta es obligatorio");
        dto.setId(null);
        return customerMapper.toDTO(customerRepository.save(customerMapper.toEntity(dto)));
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado"));
        if (dto.getFirstName() != null) c.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) c.setLastName(dto.getLastName());
        if (dto.getAccountNumber() != null) c.setAccountNumber(dto.getAccountNumber());
        if (dto.getBalance() != null) c.setBalance(dto.getBalance());
        return customerMapper.toDTO(customerRepository.save(c));
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id))
            throw new NoSuchElementException("Cliente no encontrado");
        customerRepository.deleteById(id);
    }
}
