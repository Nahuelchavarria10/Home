package com.mindhub.homebanking.services.ServicesManager;

import com.mindhub.homebanking.DTO.LoanApplicationDTO;
import com.mindhub.homebanking.DTO.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanServiceManager {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;



    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        return new ResponseEntity<>(loans.stream()
                .map(loan -> new LoanDTO(loan))
                .collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }

    public ResponseEntity<?> getLoanById(@PathVariable("id") Long id) {
        Loan loan = loanService.getLoanById(id);
        if (loan == null) {
            String notFound = "loan not found";
            return new ResponseEntity<>(notFound, HttpStatus.NOT_FOUND);
        } else {
            LoanDTO loanDTO = new LoanDTO(loan);
            return new ResponseEntity<>(loanDTO, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> applyLoan(@RequestBody LoanApplicationDTO loanApplicationDTO) {
        try {
            String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
            Client client = clientService.getClientByEmail(userMail);

            if (client == null) {
                return ResponseEntity.status(403).body("client not found");
            }
            if (loanApplicationDTO.id() == null) {
                return ResponseEntity.status(403).body("loan identifier cannot be null");
            }
            if (!(loanApplicationDTO.id() >= 1 && loanApplicationDTO.id() <= 3)) {
                return ResponseEntity.status(403).body("loan identifier not found");
            }
            if (loanApplicationDTO.amount() == null || loanApplicationDTO.amount() <= 0) {
                return ResponseEntity.status(403).body("loan amount cannot be null or less than 0");
            }
            if (loanApplicationDTO.AccountDestination().isBlank()) {
                return ResponseEntity.status(403).body("The loan destination cannot be blank.");
            }
            if (loanApplicationDTO.payments() == null || loanApplicationDTO.payments() <= 0) {
                return ResponseEntity.status(403).body("The number of payments cannot be null or less than 0.");
            }
            if (!accountService.existsAccountByNumberAndClient(loanApplicationDTO.AccountDestination(),client)) {
                return ResponseEntity.status(403).body("account does not exist or does not belong to the authenticated customer.");
            }
            if (clientLoanService.existsClientLoanByClientAndLoan(client, loanService.getLoanById(loanApplicationDTO.id()))) {
                return ResponseEntity.status(403).body("the customer has already applied for this loan");
            }

            Account accountDestination = accountService.findByNumber(loanApplicationDTO.AccountDestination());

            double additional = loanApplicationDTO.amount() * 0.2;
            double creditAmount = accountDestination.getBalance() + loanApplicationDTO.amount();

            //if Loan Mortgage
            if (loanApplicationDTO.id() == 1) {
                Loan mortgage = loanService.getLoanById(loanApplicationDTO.id());
                if (mortgage == null) {
                    return ResponseEntity.status(403).body("loan not found");
                }
                if (loanApplicationDTO.amount() > 500000) {
                    return ResponseEntity.status(403).body("The loan amount cannot be greater than 500000.");
                }
                /*if (mortgage.getMaxAmount() < loanApplicationDTO.amount()) {
                    return ResponseEntity.status(403).body("The loan amount cannot exceed the maximum amount available.");
                }*/
                if (!mortgage.getPayments().contains(loanApplicationDTO.payments())) {
                    return ResponseEntity.status(403).body("this payment is not available.");
                }

                ClientLoan clientLoan1 = new ClientLoan(loanApplicationDTO.payments(), loanApplicationDTO.amount() + additional);
                Transaction transactionDestination = new Transaction(TransactionType.CREDIT, loanApplicationDTO.amount(), mortgage.getName() + " loan approved", LocalDateTime.now());

                //mortgage.setMaxAmount(mortgage.getMaxAmount() - loanApplicationDTO.amount());

                mortgage.addClientLoan(clientLoan1);
                client.addClientLoan(clientLoan1);
                accountDestination.addTransaction(transactionDestination);
                accountDestination.setBalance(creditAmount);

                //loanService.saveLoan(mortgage);

                accountService.saveAccount(accountDestination);
                clientLoanService.saveClientLoan(clientLoan1);
                transactionService.saveTransaction(transactionDestination);
            }

            //if Loan Personal
            if (loanApplicationDTO.id() == 2) {
                Loan personal = loanService.getLoanById(loanApplicationDTO.id());
                if (personal == null) {
                    return ResponseEntity.status(403).body("loan not found");
                }
                if (loanApplicationDTO.amount() > 100000) {
                    return ResponseEntity.status(403).body("The loan amount cannot be greater than 100000.");
                }
                /*if (personal.getMaxAmount() < loanApplicationDTO.amount()) {
                    return ResponseEntity.status(403).body("The loan amount cannot exceed the maximum amount available.");
                }*/
                if (!personal.getPayments().contains(loanApplicationDTO.payments())) {
                    return ResponseEntity.status(403).body("this payment is not available.");
                }

                ClientLoan clientLoan2 = new ClientLoan(loanApplicationDTO.payments(), loanApplicationDTO.amount() + additional);
                Transaction transactionDestination = new Transaction(TransactionType.CREDIT, loanApplicationDTO.amount(), personal.getName() + " loan approved", LocalDateTime.now());

                //personal.setMaxAmount(personal.getMaxAmount() - loanApplicationDTO.amount());

                personal.addClientLoan(clientLoan2);
                client.addClientLoan(clientLoan2);
                accountDestination.addTransaction(transactionDestination);
                accountDestination.setBalance(creditAmount);

                //loanService.saveLoan(personal);

                accountService.saveAccount(accountDestination);
                clientLoanService.saveClientLoan(clientLoan2);
                transactionService.saveTransaction(transactionDestination);
            }

            // if Loan Automotive
            if (loanApplicationDTO.id() == 3){
                Loan automotive = loanService.getLoanById(loanApplicationDTO.id());
                if (automotive == null) {
                    return ResponseEntity.status(403).body("loan not found");
                }
                if (loanApplicationDTO.amount() > 300000) {
                    return ResponseEntity.status(403).body("The loan amount cannot be greater than 300000.");
                }
                /*if (automotive.getMaxAmount() < loanApplicationDTO.amount()) {
                    return ResponseEntity.status(403).body("The loan amount cannot exceed the maximum amount available.");
                }*/
                if (!automotive.getPayments().contains(loanApplicationDTO.payments())) {
                    return ResponseEntity.status(403).body("this payment is not available.");
                }

                ClientLoan clientLoan3 = new ClientLoan(loanApplicationDTO.payments(), loanApplicationDTO.amount() + additional);
                Transaction transactionDestination = new Transaction(TransactionType.CREDIT, loanApplicationDTO.amount(), automotive.getName() + " loan approved", LocalDateTime.now());

                //automotive.setMaxAmount(automotive.getMaxAmount() - loanApplicationDTO.amount());

                automotive.addClientLoan(clientLoan3);
                client.addClientLoan(clientLoan3);
                accountDestination.addTransaction(transactionDestination);
                accountDestination.setBalance(creditAmount);

                //loanService.saveLoan(automotive);

                accountService.saveAccount(accountDestination);
                clientLoanService.saveClientLoan(clientLoan3);
                transactionService.saveTransaction(transactionDestination);
            }

            return ResponseEntity.status(201).body("loan applied successfully");
        }catch (Exception e) {
            return  ResponseEntity.status(500).body("Internal server error ");
        }

    }
}
