package com.valenguard.client.game.world.item.inventory;

import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemSlotContainer;

public class InventoryUtil {

    private InventoryUtil() {
    }

    public static ItemSlotContainer getItemSlotContainer(byte inventoryByte) {
        InventoryType inventoryType = InventoryType.values()[inventoryByte];
        if (inventoryType == InventoryType.BAG_1) {
            return ActorUtil.getStageHandler().getBagWindow().getItemSlotContainer();
        } else if (inventoryType == InventoryType.BANK) {
            return ActorUtil.getStageHandler().getBankWindow().getItemSlotContainer();
        } else if (inventoryType == InventoryType.EQUIPMENT) {
            return ActorUtil.getStageHandler().getEquipmentWindow().getItemSlotContainer();
        } else if (inventoryType == InventoryType.HOT_BAR) {
            return ActorUtil.getStageHandler().getHotBar().getItemSlotContainer();
        }
        throw new RuntimeException("Impossible Case!");
    }
}
