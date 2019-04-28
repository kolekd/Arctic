package com.company.util;

import java.util.Random;

public class RandomUtil {

    private static Random random = new Random();

    public static boolean get() {
        int randNum = randomNumberInRange(0, 101);
        return randNum > 50;
    }

    public static int randomNumberInRange(int from, int to) {
        return random.nextInt((to - from) + 1) + from;
    }
}
