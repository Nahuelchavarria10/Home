package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.LoanApplicationDTO;
import com.mindhub.homebanking.DTO.LoanDTO;
import com.mindhub.homebanking.services.ServicesManager.LoanServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanServiceManager loanServiceManager;

    @GetMapping("/")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        return loanServiceManager.getAllLoans();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(@PathVariable("id") Long id) {
        return loanServiceManager.getLoanById(id);
    }

    @PostMapping("/")
    public ResponseEntity<?> applyLoan(@RequestBody LoanApplicationDTO loanApplicationDTO){
        return loanServiceManager.applyLoan(loanApplicationDTO);
    }

}
