package com.forgestorm.client.util;

import java.util.regex.Pattern;

public class StringUtils {
    public static boolean isValidName(String name) {
        String specialCharacters = "!#$%&'()*+,-./:;<=>?@[]^_`{|}~0123456789";
        String[] str2 = name.split("");
        int count = 0;
        for (int i = 0; i < str2.length; i++) {
            if (specialCharacters.contains(str2[i])) {
                count++;
            }
        }

        return name != null && count == 0;
    }

    public static String abbreviateString(String input, int maxLength) {
        if (input.length() <= maxLength)
            return input;
        else
            return input.substring(0, maxLength);
    }

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static boolean isNumeric(String stringNumber) {
        if (stringNumber == null) return false;
        return NUMERIC_PATTERN.matcher(stringNumber).matches();
    }

    public static void main(String[] args) {
        System.out.println("IsTrue: " + isNumeric("abc"));
        System.out.println("IsTrue: " + isNumeric("abc123"));
        System.out.println("IsTrue: " + isNumeric("123"));
    }
}
