package com.mindhub.homebanking;

import com.mindhub.homebanking.securityServices.UtilService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HomebankingApplicationTests {

	@Autowired
	private UtilService utilservice;

	@Test
	void contextLoads() {
	}

	@RepeatedTest(1000)
	void testRandomNumberInRange() {
		UtilService utilService = new UtilService();

		int randomNumber = utilService.getRandomNumber(100, 999);

		Assertions.assertTrue(randomNumber >= 100 && randomNumber <= 999,
				"Random number must be between 100 and 999");
	}
	@RepeatedTest(1000)
	void testGenerateCvv() {
		UtilService utilService = new UtilService();
		String cvv = utilService.generateCvv();
		Assertions.assertEquals(3, cvv.length(), "Cvv must be 3 digits");
	}

}
