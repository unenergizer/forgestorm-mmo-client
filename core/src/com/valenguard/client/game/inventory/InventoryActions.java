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
    private byte fromPosition;

    @Getter
    private byte toPosition;

    @Getter
    private byte fromWindow;

    @Getter
    private byte toWindow;

    public InventoryActions(byte inventoryActionType, InventoryType fromWindow, InventoryType toWindow, byte fromPosition, byte toPosition) {
        this.inventoryActionType = inventoryActionType;
        this.fromWindow = (byte) fromWindow.ordinal();
        this.toWindow = (byte) toWindow.ordinal();
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }
}
