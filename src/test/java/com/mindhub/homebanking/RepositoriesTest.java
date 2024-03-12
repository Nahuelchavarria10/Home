package com.mindhub.homebanking;

import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepositoriesTest {

    //ClientRepository
    @Autowired
    private ClientRepository clientRepository;

    @Test
    void contextLoads() {
    }
    @Test
    void testFindByEmail() {
        Client client = clientRepository.findByEmail("melba@mindhub.com");
        Assertions.assertNotNull(client, "Client not found");
        Assertions.assertEquals("melba@mindhub.com", client.getEmail(), "Email does not match");
    }

    //AccountRepository
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testFindByNumber() {
        Assertions.assertNotNull(accountRepository.findByNumber("VIN001"), "Account not found");
    }
    @Test
    void existsAccountByNumber() {
        String accountNumber = "VIN002";
        Assertions.assertTrue(accountRepository.existsAccountByNumber(accountNumber), "Account not exist");
    }
    @Test
    void existsAccountByNumberAndClient() {
        // Obtener el cliente
        String accountNumber = "VIN002";
        String clientEmail = "melba@mindhub.com";

        Client client = clientRepository.findByEmail(clientEmail);
        Assertions.assertNotNull(client, "Client not found");

        // Verificar si el cliente exite antes de buscar la cuenta
        Assertions.assertDoesNotThrow(() -> {
            boolean accountExists = accountRepository.existsAccountByNumberAndClient(accountNumber, client);
            Assertions.assertTrue(accountExists, "Account does not exist");
        }, "An error occurred while checking account existence");
    }

    //CardRepository
    @Autowired
    CardRepository cardRepository;
    @Test
    void testExistsCardByColorAndTypeAndClient() {
        String color = "TITANIUM";
        String type = "CREDIT";
        String clientEmail = "melba@mindhub.com";

        Client client = clientRepository.findByEmail(clientEmail);
        Assertions.assertNotNull(client, "Client not found");

        Assertions.assertDoesNotThrow(() -> {
            boolean cardExists = cardRepository.existsCardByColorAndTypeAndClient(CardColor.valueOf(color), CardType.valueOf(type), client);
            Assertions.assertTrue(cardExists, "Card does not exist");
        }, "An error occurred while checking card existence");
    }

    @Test
    void testCountByTypeAndClient() {
        String type = "DEBIT";
        String clientEmail = "ricardo@mindhub.com";

        Client client = clientRepository.findByEmail(clientEmail);
        Assertions.assertNotNull(client, "Client not found");

        Assertions.assertDoesNotThrow(() -> {
            int count = cardRepository.countByTypeAndClient(CardType.valueOf(type), client);
            Assertions.assertTrue(count <= 3 && count >= 0, "the customer has more cards than expected");
        }, "An error occurred while checking card existence");
    }

    //ClientLoanRepository
    @Autowired
    ClientLoanRepository clientLoanRepository;
    @Autowired
    LoanRepository loanRepository;

    @Test
    void testExistsClientLoanByClientAndLoan() {
        String clientEmail = "melba@mindhub.com";
        Loan loanId = loanRepository.findById(2L).orElse(null);
        Client client = clientRepository.findByEmail(clientEmail);
        Assertions.assertNotNull(client, "Client not found");
        Assertions.assertNotNull(loanId, "Loan not found");

        Assertions.assertDoesNotThrow(() -> {
            boolean loanExists = clientLoanRepository.existsClientLoanByClientAndLoan(client,loanId);
            Assertions.assertTrue(loanExists, "this customer does not have this loan");
        }, "An error occurred while checking loan existence");
    }
}
