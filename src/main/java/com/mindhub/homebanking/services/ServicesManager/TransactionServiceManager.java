package com.mindhub.homebanking.services.ServicesManager;

import com.mindhub.homebanking.DTO.TransactionApplyDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientLoanService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
public class TransactionServiceManager {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;



    public ResponseEntity<?> applyTransaction (@RequestBody TransactionApplyDTO transactionApplyDTO){

        try {
            String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
            Client client = clientService.getClientByEmail(userMail);

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

            if (!accountService.existsAccountByNumber(transactionApplyDTO.accountOrigin())) {
                return ResponseEntity.status(403).body("the source account does not exist");
            }
            if (transactionApplyDTO.accountOrigin().equals(transactionApplyDTO.AccountDestination())) {
                return ResponseEntity.status(403).body("the source and destination accounts cannot be the same.");
            }
            if (!accountService.existsAccountByNumberAndClient(transactionApplyDTO.accountOrigin(),client)) {
                return ResponseEntity.status(403).body("account does not belong to the authenticated customer");
            }
            if (!accountService.existsAccountByNumber(transactionApplyDTO.AccountDestination())) {
                return ResponseEntity.status(403).body("the recipient's account does not exist");
            }

            //account origin

            Account accountOrigin = accountService.findByNumberAndClient(transactionApplyDTO.accountOrigin(), client);

            if (transactionApplyDTO.amount() > accountOrigin.getBalance()) {
                return ResponseEntity.status(403).body("the amount of the source account is insufficient");
            }

            String descriptionNumberAccountOrigin = " " + "(source account: " + transactionApplyDTO.accountOrigin() + ")";

            Transaction transactionOrigin = new Transaction(TransactionType.DEBIT, -transactionApplyDTO.amount(), transactionApplyDTO.description() + descriptionNumberAccountOrigin, LocalDateTime.now());
            accountOrigin.addTransaction(transactionOrigin);

            double debitAmount = accountOrigin.getBalance() - transactionApplyDTO.amount();
            accountOrigin.setBalance(debitAmount);


            //account destination

            String descriptionNumberAccountDestination = " " + "(destination account: " + transactionApplyDTO.AccountDestination() + ")";

            Account accountDestination = accountService.findByNumber(transactionApplyDTO.AccountDestination());
            Transaction transactionDestination = new Transaction(TransactionType.CREDIT, transactionApplyDTO.amount(), transactionApplyDTO.description() + descriptionNumberAccountDestination , LocalDateTime.now());

            accountDestination.addTransaction(transactionDestination);

            double creditAmount = accountDestination.getBalance() + transactionApplyDTO.amount();
            accountDestination.setBalance(creditAmount);

            accountService.saveAccount(accountOrigin);
            accountService.saveAccount(accountDestination);
            transactionService.saveTransaction(transactionOrigin);
            transactionService.saveTransaction(transactionDestination);

            return ResponseEntity.status(201).body("Transaction successfully");
        }catch (Exception e){
            return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
