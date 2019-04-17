package com.valenguard.client.game.world.item;

public enum ItemStackType {
    // Main Body
    HELM,
    CHEST,
    BOOTS,
    CAPE,
    GLOVES,
    BELT,

    // Rings
    RING,
    NECKLACE,

    // Weapons
    SWORD,
    BOW,
    SHIELD,
    ARROW,

    // Generic
    GOLD,
    POTION,
    MATERIAL;

    // Trade Items
    // Skill Items

    public boolean isEquipable() {
        switch (this) {
            case HELM:
            case CHEST:
            case BOOTS:
            case CAPE:
            case GLOVES:
            case BELT:
            case RING:
            case NECKLACE:
            case SWORD:
            case BOW:
            case SHIELD:
            case ARROW:
                return true;
            default:
                return false;
        }
    }

}
