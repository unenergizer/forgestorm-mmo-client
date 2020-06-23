package com.forgestorm.client.game.screens;

public enum UserInterfaceType {
    LOGIN,
    CHARACTER_SELECT,
    GAME;

    public static UserInterfaceType getScreenType(byte screenByte) {
        for (UserInterfaceType screen : UserInterfaceType.values()) {
            if ((byte) screen.ordinal() == screenByte) {
                return screen;
            }
        }
        return null;
    }

    public byte getScreenTypeByte() {
        return (byte) this.ordinal();
    }
}
