package com.forgestorm.shared.util;

import java.util.Random;

public class RandomNumberUtil {

    private static final Random RANDOM = new Random();

    private RandomNumberUtil() {
    }

    public static int getNewRandom(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static short getNewRandom(short min, short max) {
        return (short) (RANDOM.nextInt((max - min) + 1) + min);
    }

    public static float getNewRandom(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }

}
