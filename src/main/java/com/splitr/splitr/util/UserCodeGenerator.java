package com.splitr.splitr.util;

import com.splitr.splitr.repository.UserRepository;

import java.util.Random;

public class UserCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 5;
    private static final Random RANDOM = new Random();

    public static String generateUniqueCode(UserRepository userRepository) {
        String code;
        do {
            code = generateCode();
        } while (userRepository.existsByUserCode(code)); // Benzersiz mi kontrolü
        return code;
    }

    private static String generateCode() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
