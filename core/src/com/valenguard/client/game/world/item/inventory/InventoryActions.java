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
    private byte dropInventory;
    private byte slotIndex;

    public InventoryActions(ActionType actionType, InventoryType fromWindow, InventoryType toWindow, byte fromPosition, byte toPosition) {
        this.actionType = actionType;
        this.fromWindow = fromWindow.getInventoryTypeIndex();
        this.toWindow = toWindow.getInventoryTypeIndex();
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

    public InventoryActions(ActionType actionType, byte dropInventory, byte slotIndex) {
        this.actionType = actionType;
        this.dropInventory = dropInventory;
        this.slotIndex = slotIndex;
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

        /**
         * SERVER -> CLIENT
         */
        GIVE((byte) 0x03),
        REMOVE((byte) 0x04),
        SET_BAG((byte) 0x05),
        SET_BANK((byte) 0x06),
        SET_EQUIPMENT((byte) 0x07);

        private byte getActionType;

        public static ActionType getActionType(byte inventoryActionType) {
            for (ActionType entityType : ActionType.values()) {
                if ((byte) entityType.ordinal() == inventoryActionType) return entityType;
            }
            throw new RuntimeException("Inventory type miss match! Byte Received: " + inventoryActionType);
        }
    }
}
