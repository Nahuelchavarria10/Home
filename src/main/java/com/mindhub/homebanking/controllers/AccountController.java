package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.AccountDTO;
import com.mindhub.homebanking.DTO.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.JwtUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
//@CrossOrigin(origins="*")
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtUtilService jwtUtilService;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/")
    public ResponseEntity<List<AccountDTO>> getAllAccounts(){
        List<Account> accounts= accountRepository.findAll();
        return new ResponseEntity<>(accounts.stream()
                .map(account -> new AccountDTO(account))
                .collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable("id") Long id){
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null){
            String notFound = "account not found";
            return new ResponseEntity<>(notFound,HttpStatus.NOT_FOUND);
        }
        else {
            AccountDTO accountDTO = new AccountDTO(account);
            return new ResponseEntity<>(accountDTO,HttpStatus.OK);
        }
    }

    @PostMapping("current/applyAccount")
    public ResponseEntity<?> applyAccount (){

        try {
            String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
            Client client = clientRepository.findByEmail(userMail);

            if (clienteTieneMasDeTresCuentas(client)) {
                return ResponseEntity.status(400).body("error el cliente no puede tener mas de 3 cuentas");
            }

            Account account = new Account("VIN-" + jwtUtilService.getRandomNumber(0,99999999) , LocalDate.now(),0);
            client.addAccount(account);
            accountRepository.save(account);

            return ResponseEntity.ok("Cuenta creada exitosamente");
        }catch (Exception e){
            return new ResponseEntity<>("datos incorrectos pa", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/current/accounts")
    public ResponseEntity<List<AccountDTO>> getAccounts(){
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByEmail(userMail);

        Set<Account> accounts = client.getAccounts();

        return new ResponseEntity<>(accounts.stream().map(AccountDTO::new).collect(java.util.stream.Collectors.toList()), HttpStatus.OK);
    }

    private boolean clienteTieneMasDeTresCuentas(Client client) {
        int cantidadDeCuentas = client.getAccounts().size();
        return cantidadDeCuentas >= 3;
    }

}

