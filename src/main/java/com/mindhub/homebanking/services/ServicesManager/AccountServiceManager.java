package com.mindhub.homebanking.services.ServicesManager;

import com.mindhub.homebanking.DTO.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.securityServices.JwtUtilService;
import com.mindhub.homebanking.securityServices.UtilService;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class AccountServiceManager {

    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtUtilService jwtUtilService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private ClientService clientService;

    public ResponseEntity<List<AccountDTO>> getAllAccounts(){
        return new ResponseEntity<>(accountService.getAllAccountsDTO(), HttpStatus.OK);
    }

    public ResponseEntity<?> getAccountById(@PathVariable("id") Long id){
        Account account = accountService.getAccountById(id);
        if (account == null){
            String notFound = "account not found";
            return new ResponseEntity<>(notFound,HttpStatus.NOT_FOUND);
        }
        else {
            AccountDTO accountDTO = new AccountDTO(account);
            return new ResponseEntity<>(accountDTO,HttpStatus.OK);
        }
    }
    public ResponseEntity<?> applyAccount (){

        try {
            String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
            Client client = clientService.getClientByEmail(userMail);

            if (client.getAccounts().size() >= 3) {
                return ResponseEntity.status(403).body("the customer has reached the maximum number of 3 accounts created");
            }

            int accountNumberRandom = utilService.getRandomNumber(100,99999999);

            Account account = new Account("VIN-" + accountNumberRandom , LocalDate.now(),0);
            client.addAccount(account);
            accountService.saveAccount(account);

            return ResponseEntity.status(201).body("Account successfully created");
        }catch (Exception e){
            return new ResponseEntity<>("Error processing the request.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<AccountDTO>> getAccounts(){
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientService.getClientByEmail(userMail);

        Set<Account> accounts = client.getAccounts();

        return new ResponseEntity<>(accounts.stream().map(AccountDTO::new).collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }
}
