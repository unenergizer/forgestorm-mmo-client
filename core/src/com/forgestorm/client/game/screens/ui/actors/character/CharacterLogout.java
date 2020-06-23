package com.forgestorm.client.game.screens.ui.actors.character;

public enum CharacterLogout {

    LOGOUT_CHARACTER,
    LOGOUT_SERVER;

    public static CharacterLogout getType(byte typeByte) {
        for (CharacterLogout type : CharacterLogout.values()) {
            if ((byte) type.ordinal() == typeByte) {
                return type;
            }
        }
        return null;
    }

    public byte getTypeByte() {
        return (byte) this.ordinal();
    }
}
