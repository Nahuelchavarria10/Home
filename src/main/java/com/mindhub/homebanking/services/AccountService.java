package com.mindhub.homebanking.services;

import com.mindhub.homebanking.DTO.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface AccountService {

    List<Account> getAllAccounts();
    List<AccountDTO> getAllAccountsDTO();
    Account getAccountById(Long id);
    void saveAccount(Account account);

    Account findByNumberAndClient(String number, Client client);
    Account findByNumber(String number);
    Boolean existsAccountByNumber(String number);
    Boolean existsAccountByNumberAndClient(String number, Client client);
}
