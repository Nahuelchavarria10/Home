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


            if (!accountService.existsAccountByNumberAndClient(loanApplicationDTO.AccountDestination(),client)) {
                return ResponseEntity.status(403).body("account does not exist or does not belong to the authenticated customer.");
            }
            if (clientLoanService.existsClientLoanByClientAndLoan(client, loanService.getLoanById(loanApplicationDTO.id()))) {
                return ResponseEntity.status(403).body("the customer has already applied for this loan");
            }

            if (loanApplicationDTO.amount() > loanService.getLoanById(loanApplicationDTO.id()).getMaxAmount()) {
                return ResponseEntity.status(403).body("the amount exceeds the maximum amount");
            }
            if (!loanService.getLoanById(loanApplicationDTO.id()).getPayments().contains(loanApplicationDTO.payments())) {
                return ResponseEntity.status(403).body("the selected number of payments is not available on this type of loan.");
            }

            double additional = loanApplicationDTO.amount() * 0.2;
            ClientLoan clientLoan1 = new ClientLoan(loanApplicationDTO.payments(), loanApplicationDTO.amount() + additional);

            Loan loan = loanService.getLoanById(loanApplicationDTO.id());
            loan.addClientLoan(clientLoan1);
            client.addClientLoan(clientLoan1);

            Transaction transactionDestination = new Transaction(TransactionType.CREDIT, loanApplicationDTO.amount(), loanService.getLoanById(loanApplicationDTO.id()).getName() + " loan approved", LocalDateTime.now());


            accountDestination.addTransaction(transactionDestination);

            double creditAmount = accountDestination.getBalance() + loanApplicationDTO.amount();
            accountDestination.setBalance(creditAmount);

            accountService.saveAccount(accountDestination);
            clientLoanService.saveClientLoan(clientLoan1);
            transactionService.saveTransaction(transactionDestination);
            clientService.saveClient(client);

            return ResponseEntity.status(201).body("loan applied successfully");
        }catch (Exception e) {
            return  ResponseEntity.status(500).body("Internal server error ");
        }

    }
}
