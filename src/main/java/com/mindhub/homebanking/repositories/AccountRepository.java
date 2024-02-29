package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByNumberAndClient(String number, Client client);
    Account findByNumber(String number);
    Boolean existsAccountByNumber(String number);
    Boolean existsAccountByNumberAndClient(String number, Client client);

}
