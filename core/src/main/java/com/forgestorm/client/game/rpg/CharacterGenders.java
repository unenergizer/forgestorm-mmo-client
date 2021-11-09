package com.forgestorm.client.game.rpg;

public enum CharacterGenders {
    MALE,
    FEMALE;

    public static CharacterGenders getType(byte typeByte) {
        for (CharacterGenders type : CharacterGenders.values()) {
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
