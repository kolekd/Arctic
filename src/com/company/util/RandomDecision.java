package com.company.util;

import java.util.Random;

public class RandomDecision {

    private static Random random = new Random();

    public static boolean get() {
        int randNum = random.nextInt((101 - 0) + 1) + 0;

        if (randNum > 50) {
            return true;
        }

        return false;
    }
}
