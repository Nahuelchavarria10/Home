package com.mindhub.homebanking.DTO;

import com.mindhub.homebanking.models.Account;

public record TransactionApplyDTO(String accountOrigin, Double amount, String AccountDestination, String description) {
}
