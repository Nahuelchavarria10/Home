package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.TransactionApplyDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @PostMapping("/")
    public ResponseEntity<?> applyCard ( @RequestBody TransactionApplyDTO transactionApplyDTO){

        try {
            String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
            Client client = clientRepository.findByEmail(userMail);

            if (transactionApplyDTO.accountOrigin().isBlank()) {
                return ResponseEntity.status(403).body("Account Origin is required");
            }
            if (transactionApplyDTO.amount() == null ) {
                return ResponseEntity.status(403).body("Amount is required");
            }
            if (transactionApplyDTO.AccountDestination().isBlank()) {
                return ResponseEntity.status(403).body("Account Destination is required");
            }
            if (transactionApplyDTO.description().isBlank()) {
                return ResponseEntity.status(403).body("Description is required");
            }

            if (!accountRepository.existsAccountByNumber(transactionApplyDTO.accountOrigin())) {
                return ResponseEntity.status(403).body("the source account does not exist");
            }
            if (transactionApplyDTO.accountOrigin().equals(transactionApplyDTO.AccountDestination())) {
                return ResponseEntity.status(403).body("the source and destination accounts cannot be the same.");
            }
            if (!accountRepository.existsAccountByNumberAndClient(transactionApplyDTO.accountOrigin(),client)) {
                return ResponseEntity.status(403).body("account does not belong to the authenticated customer");
            }
            if (!accountRepository.existsAccountByNumber(transactionApplyDTO.AccountDestination())) {
                return ResponseEntity.status(403).body("the recipient's account does not exist");
            }
            if (!accountRepository.existsAccountByNumber(transactionApplyDTO.AccountDestination())) {
                return ResponseEntity.status(403).body("the recipient's account does not exist");
            }

            //account origin

            Account accountOrigin = accountRepository.findByNumberAndClient(transactionApplyDTO.accountOrigin(), client);

            if (transactionApplyDTO.amount() > accountOrigin.getBalance()) {
                return ResponseEntity.status(403).body("the amount of the source account is insufficient");
            }

            String descriptionNumberAccountOrigin = " " + "(source account: " + transactionApplyDTO.accountOrigin() + ")";

            Transaction transactionOrigin = new Transaction(TransactionType.DEBIT, -transactionApplyDTO.amount(), transactionApplyDTO.description() + descriptionNumberAccountOrigin, LocalDateTime.now());
            accountOrigin.addTransaction(transactionOrigin);

            double debit = accountOrigin.getBalance() - transactionApplyDTO.amount();
            accountOrigin.setBalance(debit);


            //account destination

            String descriptionNumberAccountDestination = " " + "(destination account: " + transactionApplyDTO.AccountDestination() + ")";

            Account accountDestination = accountRepository.findByNumber(transactionApplyDTO.AccountDestination());
            Transaction transactionDestination = new Transaction(TransactionType.CREDIT, transactionApplyDTO.amount(), transactionApplyDTO.description() + descriptionNumberAccountDestination , LocalDateTime.now());

            accountDestination.addTransaction(transactionDestination);

            double credit = accountDestination.getBalance() + transactionApplyDTO.amount();
            accountDestination.setBalance(credit);

            transactionRepository.save(transactionOrigin);
            transactionRepository.save(transactionDestination);

            return ResponseEntity.status(201).body("Transaction successfully");
        }catch (Exception e){
            return new ResponseEntity<>("Error processing the request.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}