package com.valenguard.client.util;

public class Preconditions {

    public Preconditions() {
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) throw new IllegalArgumentException(errorMessage);
    }

    public static void checkNotNull(Object object, String errorMessage) {
        if (object == null) throw new NullPointerException(errorMessage);
    }

}
