package com.valenguard.client.game.rpg;

public enum FactionTypes {
    THE_EMPIRE,
    IMPERIAL_LEGION,
    BURNING_CITY,
    SHORES_OF_WRATH,
    MOUNTAIN_DWELLERS_OF_THE_NORTH,
    FROZEN_THRONE;

    public byte getFactionTypeByte() {
        return (byte) this.ordinal();
    }

    public static FactionTypes getFactionType(byte entityTypeByte) {
        for (FactionTypes factionTypes : FactionTypes.values()) {
            if ((byte) factionTypes.ordinal() == entityTypeByte) {
                return factionTypes;
            }
        }
        return null;
    }
}
