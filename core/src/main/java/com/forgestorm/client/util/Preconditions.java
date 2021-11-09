package com.forgestorm.client.util;

public class Preconditions {

    private Preconditions() {
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) throw new IllegalArgumentException(errorMessage);
    }

    public static void checkNotNull(Object object, String errorMessage) {
        if (object == null) throw new NullPointerException(errorMessage);
    }

}
