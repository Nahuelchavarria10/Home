package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.AccountDTO;
import com.mindhub.homebanking.services.ServicesManager.AccountServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountServiceManager accountServiceManager;


    @GetMapping("/")
    public ResponseEntity<List<AccountDTO>> getAllAccounts(){
        return accountServiceManager.getAllAccounts();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable("id") Long id){
        return accountServiceManager.getAccountById(id);
    }

    @PostMapping("current/applyAccount")
    public ResponseEntity<?> applyAccount(){
        return accountServiceManager.applyAccount();
    }

    @GetMapping("/current")
    public ResponseEntity<List<AccountDTO>> getAccounts(){
        return accountServiceManager.getAccounts();
    }

}

