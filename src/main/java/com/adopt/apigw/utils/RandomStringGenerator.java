package com.adopt.apigw.utils;

import java.util.Random;

public class RandomStringGenerator {
    public static String generate(String values, int length) {
        Random rndm_method = new Random();

        char[] password = new char[length];

        for (int i = 0; i < length; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            password[i] =
                    values.charAt(rndm_method.nextInt(values.length()));

        }
        return new String(password);
    }
}
