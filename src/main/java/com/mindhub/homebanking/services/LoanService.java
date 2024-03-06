package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Loan;
import java.util.List;

public interface LoanService {
    void saveLoan(Loan loan);

    List<Loan> getAllLoans();

    Loan getLoanById(Long id);
}
