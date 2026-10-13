package com.udea.bancoudea.mapper;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDTO toDTO(Transaction transaction);
}
