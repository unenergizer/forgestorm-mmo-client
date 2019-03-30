package com.valenguard.client.game.world.item.inventory;

import lombok.Getter;

public class InventoryActions {

    /**
     *   SHARED
     */
    public static final byte MOVE = 0x00;

    /**
     *   CLIENT -> SERVER
     */
    public static final byte DROP = 0x01;
    public static final byte USE = 0x02;

    /**
     *   SERVER -> CLIENT
     */
    public static final byte GIVE = 0x03;
    public static final byte REMOVE = 0x04;
    public static final byte SET_BAG = 0x05;
    public static final byte SET_BANK = 0x06;
    public static final byte SET_EQUIPMENT = 0x07;

    @Getter
    private byte inventoryActionType;

    @Getter
    private byte fromPosition;

    @Getter
    private byte toPosition;

    @Getter
    private byte fromWindow;

    @Getter
    private byte toWindow;

    @Getter
    byte dropInventory;

    @Getter
    byte slotIndex;

    public InventoryActions(byte inventoryActionType, InventoryType fromWindow, InventoryType toWindow, byte fromPosition, byte toPosition) {
        this.inventoryActionType = inventoryActionType;
        this.fromWindow = fromWindow.getInventoryTypeIndex();
        this.toWindow = toWindow.getInventoryTypeIndex();
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

    public InventoryActions(byte inventoryActionType, byte dropInventory, byte slotIndex) {
        this.inventoryActionType = inventoryActionType;
        this.dropInventory = dropInventory;
        this.slotIndex = slotIndex;
    }
}
