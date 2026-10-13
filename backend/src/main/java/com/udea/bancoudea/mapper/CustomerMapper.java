package com.udea.bancoudea.mapper;

import com.udea.bancoudea.DTO.CustomerDTO;
import com.udea.bancoudea.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO customerDTO);
}
