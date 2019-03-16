package com.valenguard.client.game.inventory;

public enum WindowMovementInfo {
    FROM_BAG_TO_BAG,
    FROM_BAG_TO_EQUIPMENT,
    FROM_EQUIPMENT_TO_BAG,
    FROM_EQUIPMENT_TO_EQUIPMENT;

    public InventoryType getFromWindow() {
        switch (this) {
            case FROM_BAG_TO_BAG:
                return InventoryType.BAG_1;
            case FROM_BAG_TO_EQUIPMENT:
                return InventoryType.BAG_1;
            case FROM_EQUIPMENT_TO_BAG:
                return InventoryType.EQUIPMENT;
            case FROM_EQUIPMENT_TO_EQUIPMENT:
                return InventoryType.EQUIPMENT;
        }
        throw new RuntimeException("Must implement all cases.");
    }
}
