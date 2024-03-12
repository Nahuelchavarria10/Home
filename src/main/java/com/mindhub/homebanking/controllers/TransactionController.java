package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.TransactionApplyDTO;
import com.mindhub.homebanking.services.ServicesManager.TransactionServiceManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionServiceManager transactionServiceManager;

    @Transactional
    @PostMapping("/")
    public ResponseEntity<?> applyTransaction ( @RequestBody TransactionApplyDTO transactionApplyDTO){
        return transactionServiceManager.applyTransaction(transactionApplyDTO);
    }

}