package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.LoanDTO;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api/loans")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;

    @GetMapping("/")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return new ResponseEntity<>(loans.stream()
                .map(loan -> new LoanDTO(loan))
                .collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(@PathVariable("id") Long id) {
        Loan loan = loanRepository.findById(id).orElse(null);
        if (loan == null) {
            String notFound = "loan not found";
            return new ResponseEntity<>(notFound, HttpStatus.NOT_FOUND);
        } else {
            LoanDTO loanDTO = new LoanDTO(loan);
            return new ResponseEntity<>(loanDTO, HttpStatus.OK);
        }
    }
}
