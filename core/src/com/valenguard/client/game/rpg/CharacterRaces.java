package com.valenguard.client.game.rpg;

public enum CharacterRaces {

    HUMAN,
    FROG,
    TURTLE;

    public static CharacterRaces getType(byte typeByte) {
        for (CharacterRaces type : CharacterRaces.values()) {
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
