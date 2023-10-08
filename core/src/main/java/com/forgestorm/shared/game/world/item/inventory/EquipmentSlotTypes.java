package com.forgestorm.shared.game.world.item.inventory;

import com.forgestorm.shared.game.world.item.ItemStackType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentSlotTypes {

    // Main Body
    HELM((byte) 0, new ItemStackType[]{ItemStackType.HELM}),
    CHEST((byte) 1, new ItemStackType[]{ItemStackType.CHEST}),
    PANTS((byte) 2, new ItemStackType[]{ItemStackType.PANTS}),
    BOOTS((byte) 3, new ItemStackType[]{ItemStackType.SHOES}),
    CAPE((byte) 4, new ItemStackType[]{ItemStackType.CAPE}),
    GLOVES((byte) 5, new ItemStackType[]{ItemStackType.GLOVES}),
    BELT((byte) 6, new ItemStackType[]{ItemStackType.BELT}),

    // Trinket
    RING_0((byte) 7, new ItemStackType[]{ItemStackType.RING}),
    RING_1((byte) 8, new ItemStackType[]{ItemStackType.RING}),
    NECKLACE((byte) 9, new ItemStackType[]{ItemStackType.NECKLACE}),

    // Weapons
    WEAPON((byte) 10, new ItemStackType[]{ItemStackType.SWORD, ItemStackType.BOW}),
    SHIELD((byte) 11, new ItemStackType[]{ItemStackType.SHIELD}),
    AMMO((byte) 12, new ItemStackType[]{ItemStackType.ARROW});

    /**
     * The server representation for this said equipment slot.
     */
    private final byte slotIndex;

    /**
     * The types of items that are accepted in said equipment slot.
     */
    private final ItemStackType[] acceptedItemStackTypes;
}
