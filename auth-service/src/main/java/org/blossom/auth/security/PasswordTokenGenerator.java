package org.blossom.auth.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PasswordTokenGenerator {
    public String generateToken() {
        int min = 100000;
        int max = 999999;
        int randomNumber = ThreadLocalRandom.current().nextInt(min, max + 1);
        return String.valueOf(randomNumber);
    }
}