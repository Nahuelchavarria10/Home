package com.mindhub.homebanking.securityServices;

import org.springframework.stereotype.Service;

@Service
public class UtilService {
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

    public String generateCVV() {
        int randomNumber = getRandomNumber(0, 999);
        return String.format("%03d", randomNumber);
    }
}
