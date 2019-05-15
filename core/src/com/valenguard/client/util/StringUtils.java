package com.valenguard.client.util;

public class StringUtils {
    public static boolean isValidName(String name) {
        String specialCharacters = "!#$%&'()*+,-./:;<=>?@[]^_`{|}~0123456789";
        String str2[] = name.split("");
        int count = 0;
        for (int i = 0; i < str2.length; i++) {
            if (specialCharacters.contains(str2[i])) {
                count++;
            }
        }

        if (name != null && count == 0) {
            return true;
        } else {
            return false;
        }
    }
}
