package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.DTO.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

    @RestController
    @RequestMapping("/api/accounts")
    public class AccountController {
        @Autowired
        private AccountRepository accountRepository;
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
    }

