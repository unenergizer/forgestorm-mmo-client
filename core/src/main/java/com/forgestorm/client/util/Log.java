package com.forgestorm.client.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Pretty print logging to the console.
 */
@SuppressWarnings("rawtypes")
public class Log {

    private final static String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN, Locale.US);

    private final static String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String[] COLORS = {
            ConsoleColors.PURPLE,
            ConsoleColors.RED,
            ConsoleColors.YELLOW,
            ConsoleColors.BLUE,
            ConsoleColors.CYAN,
            ConsoleColors.GREEN,
    };

    private Log() {
    }

    public static void println(Class clazz, String message) {
        println(clazz, message, false, true);
    }

    public static void println(Class clazz, String message, boolean isError) {
        println(clazz, message, isError, true);
    }

    public static void println(Class clazz, String message, boolean isError, boolean print) {
        if (!print) return;
        if (isError) System.err.println(buildMessage(clazz, message));
        else System.out.println(buildMessage(clazz, message));
    }

    private static String buildMessage(Class clazz, String message) {
        Date date = new Date();
        String className = clazz.getSimpleName();
        return ConsoleColors.BLACK_BRIGHT + DATE_FORMAT.format(date) + "  " +
                getColor(className) + className + "  " + ConsoleColors.RESET + message + ConsoleColors.RESET;
    }

    public static void println(boolean print) {
        if (print) System.out.println();
    }

    /**
     * Gets a fancy color based on the first letter of a word.
     *
     * @param word The word we want to get a color for.
     * @return A {@link ConsoleColors} based on the first letter of the word.
     */
    private static String getColor(String word) {
        String firstLetter = word.substring(0, 1);
        int letterIndex = LETTERS.indexOf(firstLetter.toUpperCase());

        if (letterIndex < COLORS.length) {
            return COLORS[letterIndex];
        } else if (letterIndex % COLORS.length == 0) {
            return COLORS[0];
        } else if (letterIndex % COLORS.length == 1) {
            return COLORS[1];
        } else if (letterIndex % COLORS.length == 2) {
            return COLORS[2];
        } else if (letterIndex % COLORS.length == 3) {
            return COLORS[3];
        } else if (letterIndex % COLORS.length == 4) {
            return COLORS[4];
        } else {
            return COLORS[5];
        }
    }
}
