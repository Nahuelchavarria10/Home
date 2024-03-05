package com.mindhub.homebanking.DTO;

public record LoanApplicationDTO(Long id, Double amount, Integer payments, String AccountDestination) {
}
