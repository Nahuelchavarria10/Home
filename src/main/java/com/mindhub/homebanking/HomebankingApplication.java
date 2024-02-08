package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {SpringApplication.run(HomebankingApplication.class, args);}
	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository){
		return args -> {
			Client melba = new Client("Melba","Morel","melba@mindhub.com");
			Client messi = new Client("Lionel","Messi","messi@mindhub.com");
			Client cristiano = new Client("Cristiano","Ronaldo","cristiano@mindhub.com");
			Client juan = new Client("juan","Perez","juan@mindhub.com");
			clientRepository.save(melba);
			clientRepository.save(messi);
			clientRepository.save(cristiano);
			clientRepository.save(juan);
			Account account1 = new Account("VIN001",LocalDate.now(),5000);
			Account account2 = new Account("VIN002",LocalDate.now(),7500);
			Account account3 = new Account("CR7000",LocalDate.now(),9000);
			Account account4 = new Account("JPN004",LocalDate.now(),4500);
			Account account5 = new Account("LM1000",LocalDate.now(),10000);
			Account account6 = new Account("CR7002",LocalDate.now(),3500);
			melba.addAccount(account1);
			melba.addAccount(account2);
			cristiano.addAccount(account3);
			cristiano.addAccount(account6);
			juan.addAccount(account4);
			messi.addAccount(account5);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account4);
			accountRepository.save(account3);
			accountRepository.save(account6);
			accountRepository.save(account5);
			Transaction transactionLM1000 = new Transaction(TransactionType.CREDIT,1000.99,"Adidas", LocalDateTime.now());
			Transaction transactionJPN004 = new Transaction(TransactionType.CREDIT,650.50,"Google", LocalDateTime.now());
			Transaction transactionVIN001 = new Transaction(TransactionType.DEBIT,-400.50,"MercadoLibre", LocalDateTime.now());
			account5.addTransaction(transactionLM1000);
			account1.addTransaction(transactionVIN001);
			account4.addTransaction(transactionJPN004);
			transactionRepository.save(transactionLM1000);
			transactionRepository.save(transactionVIN001);
			transactionRepository.save(transactionJPN004);
			Loan loan1 = new Loan("Mortgage",500000, List.of(12,36,48,60));
			Loan loan2 = new Loan("Personal",100000, List.of(6,12,24));
			Loan loan3 = new Loan("Automotive",300000, List.of(6,12,24,36));
			ClientLoan clientLoan1 = new ClientLoan(12,450000);
			ClientLoan clientLoan2 = new ClientLoan(24,50000);
			ClientLoan clientLoan3 = new ClientLoan(6,185750);
			loan1.addClientLoan(clientLoan1);
			loan2.addClientLoan(clientLoan2);
			loan3.addClientLoan(clientLoan3);
			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);
			melba.addClientLoan(clientLoan2);
			cristiano.addClientLoan(clientLoan1);
			juan.addClientLoan(clientLoan3);
			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
		};
	}
}
