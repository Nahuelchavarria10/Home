package com.mindhub.homebanking.DTO;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientDTO {
    private Long id;
    private String firstName,lastName,email;
    private Set<AccountDTO> accountDTOS;

    public ClientDTO( Client client) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accountDTOS = accountDTOS(client.getAccounts());
    }

    public ClientDTO() {
    }

    private Set<AccountDTO> accountDTOS(Set<Account> accounts) {
        return (Set<AccountDTO>) accounts.stream().map(AccountDTO::new).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<AccountDTO> getAccountDTOS() {
        return accountDTOS;
    }
}
