package com.valenguard.client.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private final static String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private final static DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

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
        return dateFormat.format(date) + "  [" + clazz.getSimpleName() + "] " + message;
    }

    public static void println(boolean print) {
        if (print) System.out.println();
    }
}
