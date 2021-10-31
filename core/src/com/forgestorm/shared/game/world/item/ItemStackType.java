package com.forgestorm.shared.game.world.item;

import com.forgestorm.shared.game.world.entities.AppearanceType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemStackType {
    // Main Body
    HELM(AppearanceType.HELM_TEXTURE),
    CHEST(AppearanceType.CHEST_TEXTURE),
    PANTS(AppearanceType.PANTS_TEXTURE),
    SHOES(AppearanceType.SHOES_TEXTURE),
    CAPE,
    GLOVES(AppearanceType.GLOVES_COLOR),
    BELT,

    // Rings
    RING,
    NECKLACE,

    // Weapons
    SWORD(AppearanceType.LEFT_HAND),
    BOW(AppearanceType.LEFT_HAND),
    SHIELD(AppearanceType.RIGHT_HAND),
    ARROW,

    // Generic
    GOLD,
    POTION,
    MATERIAL,

    // Skill Items
    BOOK_SKILL;

    private AppearanceType appearanceType;

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

    ItemStackType() {
    }
}
