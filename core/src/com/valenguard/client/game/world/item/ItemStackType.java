package com.valenguard.client.game.world.item;

public enum ItemStackType {
    // Main Body
    HELM,
    CHEST,
    PANTS,
    SHOES,
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
            case PANTS:
            case SHOES:
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

    public boolean isConsumable() {
        switch (this) {
            case POTION:
                return true;
            default:
                return false;
        }
    }

}
