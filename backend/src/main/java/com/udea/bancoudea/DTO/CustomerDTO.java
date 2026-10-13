package com.udea.bancoudea.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private Double balance;
}
