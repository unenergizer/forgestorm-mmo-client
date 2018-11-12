package com.valenguard.client.game.entities;

public enum EntityType {
    CLIENT_PLAYER,
    PLAYER,
    NPC,
    ITEM;

    public static EntityType getEntityType(byte entityTypeByte) {
        for (EntityType entityType : EntityType.values()) {
            if ((byte) entityType.ordinal() == entityTypeByte) {
                return entityType;
            }
        }
        return null;
    }

    public byte getEntityTypeByte() {
        return (byte) this.ordinal();
    }
}
