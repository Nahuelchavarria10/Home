package com.mindhub.homebanking.services.ServicesImpl;

import com.mindhub.homebanking.DTO.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<AccountDTO> getAllAccountsDTO() {
        return getAllAccounts().stream().map(AccountDTO::new).toList();
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Account findByNumberAndClient(String number, Client client) {
        return accountRepository.findByNumberAndClient(number, client);
    }

    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public Boolean existsAccountByNumber(String number) {
        return accountRepository.existsAccountByNumber(number);
    }

    @Override
    public Boolean existsAccountByNumberAndClient(String number, Client client) {
        return accountRepository.existsAccountByNumberAndClient(number, client);
    }
}
