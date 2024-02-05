package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {SpringApplication.run(HomebankingApplication.class, args);}
	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository){
		return args -> {
			Client melba = new Client("Melba","Morel","melba@mindhub.com");
			Client messi = new Client("Lionel","Messi","messi@mindhub.com");
			Client cristiano = new Client("Cristiano","Ronaldo","cristiano@mindhub.com");
			Client juan = new Client("juan","Perez","juan@mindhub.com");
			clientRepository.save(melba);
			clientRepository.save(messi);
			clientRepository.save(cristiano);
			clientRepository.save(juan);
			Account account = new Account("VIN001",LocalDate.now(),5000);
			Account account2 = new Account("VIN002",LocalDate.now(),7500);
			Account account3 = new Account("CR7000",LocalDate.now(),9000);
			Account account4 = new Account("JPN004",LocalDate.now(),4500);
			Account account5 = new Account("LM1000",LocalDate.now(),10000);
			Account account6 = new Account("CR7002",LocalDate.now(),3500);
			melba.addAccount(account);
			melba.addAccount(account2);
			cristiano.addAccount(account3);
			cristiano.addAccount(account6);
			juan.addAccount(account4);
			messi.addAccount(account5);
			accountRepository.save(account);
			accountRepository.save(account2);
			accountRepository.save(account4);
			accountRepository.save(account3);
			accountRepository.save(account6);
			accountRepository.save(account5);
		};
	}
}
