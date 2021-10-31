package com.forgestorm.shared.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    private StringUtil() {
    }

    /**
     * Test to see if the string only contains numbers.
     *
     * @param stringNumber The string we want to test.
     * @return True if all numbers, otherwise false.
     */
    public static boolean isNumeric(String stringNumber) {
        if (stringNumber == null) return false;
        return NUMERIC_PATTERN.matcher(stringNumber).matches();
    }

    /**
     * Takes an enum name like "SOME_GENERIC_NAME" and cleans it up to look
     * something like this "Some generic name"
     *
     * @param enumName The name to clean
     * @return A cleaned up name
     */
    public static String enumNameClean(String enumName) {
        // Clean the name
        List<String> wordList = new ArrayList<String>(3);
        for (String word : enumName.split("_")) {
            String name = word.toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            wordList.add(name);
        }

        // Build the name String
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < wordList.size(); i++) {
            name.append(wordList.get(i));
            if (i != wordList.size() - 1) name.append(" ");
        }

        return name.toString();
    }

    /**
     * Test to see if the name entered is a valid name.
     *
     * @param name The name that was entered.
     * @return True if the name doesn't contain any invalid characters.
     */
    public static boolean isValidName(String name) {
        final String specialCharacters = "!#$%&'()*+,-./:;<=>?@[]^_`{|}~0123456789";
        String[] str2 = name.split("");
        int count = 0;
        for (String s : str2) if (specialCharacters.contains(s)) count++;
        return count == 0;
    }

    public static String abbreviateString(String input, int maxLength) {
        if (input.length() <= maxLength)
            return input;
        else
            return input.substring(0, maxLength);
    }
}
