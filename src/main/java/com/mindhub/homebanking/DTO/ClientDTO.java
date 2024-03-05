package com.mindhub.homebanking.DTO;

import com.mindhub.homebanking.models.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientDTO {
    private Long id;
    private String firstName,lastName,email;
    private Set<AccountDTO> accounts;
    private Set<CardDTO> cards;
    private Set<ClientLoanDTO> loans;


    public ClientDTO( Client client) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accounts = client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toSet());
        this.cards = client.getCards().stream().map(card -> new CardDTO(card)).collect(Collectors.toSet());
        this.loans = client.getClientLoans().stream().map(clientLoan -> new ClientLoanDTO(clientLoan)).collect(Collectors.toSet());

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

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public Set<CardDTO> getCards() { return cards; }

    public Set<ClientLoanDTO> getLoans() { return loans; } // I renamed the "getClientLoan" method to "getLoans"

}
