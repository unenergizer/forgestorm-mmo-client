package com.forgestorm.client.util;

import java.util.Random;

public class RandomUtil {

    private static final Random RANDOM = new Random();

    private RandomUtil() {
    }

    public static int getNewRandom(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static short getNewRandom(short min, short max) {
        return (short) (RANDOM.nextInt((max - min) + 1) + min);
    }

}
