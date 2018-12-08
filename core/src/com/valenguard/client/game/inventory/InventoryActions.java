package com.valenguard.client.game.inventory;

import lombok.Getter;

public class InventoryActions {

    /**
     *   CLIENT -> SERVER
     */
    public static final byte MOVE = 0x00;
    public static final byte DROP = 0x01;
    public static final byte USE = 0x02;

    /**
     *   SERVER -> CLIENT
     */
    public static final byte GIVE = 0x03;

    @Getter
    private byte inventoryActionType;

    @Getter
    private byte clickedPosition;

    @Getter
    private byte toPosition;

    public InventoryActions(byte inventoryActionType, byte clickedPosition) {
        this.inventoryActionType = inventoryActionType;
        this.clickedPosition = clickedPosition;
    }

    public InventoryActions(byte inventoryActionType, byte clickedPosition, byte toPosition) {
        this.inventoryActionType = inventoryActionType;
        this.clickedPosition = clickedPosition;
        this.toPosition = toPosition;
    }
}
