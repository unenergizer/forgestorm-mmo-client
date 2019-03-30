package com.valenguard.client.game.world.item.inventory;

public class InventoryMovementUtil {

    private InventoryMovementUtil() {
    }

    public static InventoryMoveType getWindowMovementInfo(InventoryType sourceType, InventoryType targetType) {
        for (InventoryMoveType movementInfo : InventoryMoveType.values()) {
            if (sourceType == movementInfo.getFromWindow() && targetType == movementInfo.getToWindow()) {
                return movementInfo;
            }
        }
        return null;
    }
}
