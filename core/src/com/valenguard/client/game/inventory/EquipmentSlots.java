package com.valenguard.client.game.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentSlots {
    // Main Body
    HELM((byte) 0, new ItemStackType[]{ItemStackType.HELM}),
    CHEST((byte) 5, new ItemStackType[]{ItemStackType.CHEST}),
    BOOTS((byte) 9, new ItemStackType[]{ItemStackType.BOOTS}),
    CAPE((byte) 3, new ItemStackType[]{ItemStackType.CAPE}),
    GLOVES((byte) 6, new ItemStackType[]{ItemStackType.GLOVES}),
    BELT((byte) 8, new ItemStackType[]{ItemStackType.BELT}),

    // Rings
    RING_0((byte) 4, new ItemStackType[]{ItemStackType.RING}),
    RING_1((byte) 7, new ItemStackType[]{ItemStackType.RING}),
    NECKLACE((byte) 2, new ItemStackType[]{ItemStackType.NECKLACE}),

    // Weapons
    WEAPON((byte) 10, new ItemStackType[]{ItemStackType.SWORD, ItemStackType.BOW}),
    SHIELD((byte) 11, new ItemStackType[]{ItemStackType.SHIELD}),
    AMMO((byte) 1, new ItemStackType[]{ItemStackType.ARROW});

    /**
     * The server representation for this said equipment slot.
     */
    private byte slotIndex;

    /**
     * The types of items that are accepted in said equipment slot.
     */
    private ItemStackType[] acceptedItemStackTypes;
}
