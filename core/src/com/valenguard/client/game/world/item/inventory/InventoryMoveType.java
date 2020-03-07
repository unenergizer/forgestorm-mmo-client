package com.valenguard.client.game.world.item.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InventoryMoveType {
    FROM_BAG_TO_BAG(InventoryType.BAG_1, InventoryType.BAG_1),
    FROM_BAG_TO_BANK(InventoryType.BAG_1, InventoryType.BANK),
    FROM_BAG_TO_EQUIPMENT(InventoryType.BAG_1, InventoryType.EQUIPMENT),
    FROM_BAG_TO_HOT_BAR(InventoryType.BAG_1, InventoryType.HOT_BAR),

    FROM_BANK_TO_BAG(InventoryType.BANK, InventoryType.BAG_1),
    FROM_BANK_TO_BANK(InventoryType.BANK, InventoryType.BANK),
    FROM_BANK_TO_EQUIPMENT(InventoryType.BANK, InventoryType.EQUIPMENT),
    FROM_BANK_TO_HOT_BAR(InventoryType.BANK, InventoryType.HOT_BAR),

    FROM_EQUIPMENT_TO_BAG(InventoryType.EQUIPMENT, InventoryType.BAG_1),
    FROM_EQUIPMENT_TO_BANK(InventoryType.EQUIPMENT, InventoryType.BANK),
    FROM_EQUIPMENT_TO_EQUIPMENT(InventoryType.EQUIPMENT, InventoryType.EQUIPMENT),
    FROM_EQUIPMENT_TO_HOT_BAR(InventoryType.EQUIPMENT, InventoryType.HOT_BAR),

    FROM_HOT_BAR_TO_BAG(InventoryType.HOT_BAR, InventoryType.BAG_1),
    FROM_HOT_BAR_TO_BANK(InventoryType.HOT_BAR, InventoryType.BANK),
    FROM_HOT_BAR_TO_EQUIPMENT(InventoryType.HOT_BAR, InventoryType.EQUIPMENT),
    FROM_HOT_BAR_TO_HOT_BAR(InventoryType.HOT_BAR, InventoryType.HOT_BAR);

    private InventoryType fromWindow;
    private InventoryType toWindow;
}
