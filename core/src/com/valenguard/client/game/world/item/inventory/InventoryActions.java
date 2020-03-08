package com.valenguard.client.game.world.item.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class InventoryActions {

    private ActionType actionType;
    private byte fromPosition;
    private byte toPosition;
    private byte fromWindow;
    private byte toWindow;
    private byte interactInventory;
    private byte slotIndex;

    public InventoryActions(ActionType actionType, byte fromWindow, byte toWindow, byte fromPosition, byte toPosition,
                            byte interactInventory, byte slotIndex) {
        this.actionType = actionType;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.fromWindow = fromWindow;
        this.toWindow = toWindow;
        this.interactInventory = interactInventory;
        this.slotIndex = slotIndex;
    }

    public InventoryActions(ActionType actionType, InventoryType fromWindow, InventoryType toWindow, byte fromPosition, byte toPosition) {
        this.actionType = actionType;
        this.fromWindow = fromWindow.getInventoryTypeIndex();
        this.toWindow = toWindow.getInventoryTypeIndex();
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

    public InventoryActions(ActionType actionType, byte interactInventory, byte slotIndex) {
        this.actionType = actionType;
        this.interactInventory = interactInventory;
        this.slotIndex = slotIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InventoryActions)) return false;
        InventoryActions otherAction = (InventoryActions) o;

        if (otherAction.actionType != actionType) return false;

        return otherAction.fromPosition == fromPosition &&
                otherAction.toPosition == toPosition &&
                otherAction.fromWindow == fromWindow &&
                otherAction.toWindow == toWindow &&
                otherAction.interactInventory == interactInventory &&
                otherAction.slotIndex == slotIndex;
    }

    @Override
    public String toString() {
        return String.format("action=%s, fromPosition=%s, toPosition=%s, fromWindow=%s, toWindow=%s, interactInventory=%s, slotIndex=%s",
                actionType, fromPosition, toPosition, fromWindow, toWindow, interactInventory, slotIndex);
    }

    @Getter
    @AllArgsConstructor
    public enum ActionType {
        /**
         * SHARED
         */
        MOVE((byte) 0x00),

        /**
         * CLIENT -> SERVER
         */
        DROP((byte) 0x01),
        USE((byte) 0x02),
        CONSUME((byte) 0x03),

        /**
         * SERVER -> CLIENT
         */
        REMOVE((byte) 0x04),
        SET((byte) 0x05);

        private byte getActionType;

        public static ActionType getActionType(byte inventoryActionType) {
            for (ActionType entityType : ActionType.values()) {
                if ((byte) entityType.ordinal() == inventoryActionType) return entityType;
            }
            throw new RuntimeException("Inventory type miss match! Byte Received: " + inventoryActionType);
        }
    }
}
