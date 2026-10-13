package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.DTO.TransferRequestDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.entity.Transaction;
import com.udea.bancoudea.mapper.TransactionMapper;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              CustomerRepository customerRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public TransactionDTO transferMoney(TransferRequestDTO req) {
        if (req.getSenderAccountNumber() == null || req.getReceiverAccountNumber() == null)
            throw new IllegalArgumentException("Las cuentas no pueden ser nulas");
        if (req.getAmount() == null || req.getAmount() <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        if (req.getSenderAccountNumber().equals(req.getReceiverAccountNumber()))
            throw new IllegalArgumentException("No puedes transferir a la misma cuenta");

        Customer sender = customerRepository.findByAccountNumber(req.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));
        Customer receiver = customerRepository.findByAccountNumber(req.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));

        if (sender.getBalance() < req.getAmount())
            throw new IllegalArgumentException("Saldo insuficiente");

        sender.setBalance(sender.getBalance() - req.getAmount());
        receiver.setBalance(receiver.getBalance() + req.getAmount());
        customerRepository.save(sender);
        customerRepository.save(receiver);

        Transaction t = new Transaction();
        t.setSenderAccountNumber(sender.getAccountNumber());
        t.setReceiverAccountNumber(receiver.getAccountNumber());
        t.setAmount(req.getAmount());
        t.setTimestamp(LocalDateTime.now());
        return transactionMapper.toDTO(transactionRepository.save(t));
    }

    public List<TransactionDTO> getAll() {
        return transactionRepository.findAll().stream().map(transactionMapper::toDTO).toList();
    }

    public TransactionDTO getById(Long id) {
        return transactionRepository.findById(id).map(transactionMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Transacción no encontrada"));
    }

    public List<TransactionDTO> getTransactionsForAccount(String accountNumber) {
        return transactionRepository
                .findBySenderAccountNumberOrReceiverAccountNumberOrderByTimestampDesc(accountNumber, accountNumber)
                .stream().map(transactionMapper::toDTO).toList();
    }
}
