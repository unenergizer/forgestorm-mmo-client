package com.forgestorm.shared.game.world.item.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemSlotContainer;

public class InventoryUtil {

    private InventoryUtil() {
    }

    public static ItemSlotContainer getItemSlotContainer(ClientMain clientMain, byte inventoryByte) {
        InventoryType inventoryType = InventoryType.values()[inventoryByte];
        if (inventoryType == InventoryType.BAG_1) {
            return clientMain.getStageHandler().getBagWindow().getItemSlotContainer();
        } else if (inventoryType == InventoryType.BANK) {
            return clientMain.getStageHandler().getBankWindow().getItemSlotContainer();
        } else if (inventoryType == InventoryType.EQUIPMENT) {
            return clientMain.getStageHandler().getEquipmentWindow().getItemSlotContainer();
        } else if (inventoryType == InventoryType.HOT_BAR) {
            return clientMain.getStageHandler().getHotBar().getItemSlotContainer();
        }
        throw new RuntimeException("Impossible Case!");
    }

    public static Actor getItemStackTypeActor(ClientMain clientMain, byte inventoryByte) {
        InventoryType inventoryType = InventoryType.values()[inventoryByte];
        if (inventoryType == InventoryType.BAG_1) {
            return clientMain.getStageHandler().getBagWindow();
        } else if (inventoryType == InventoryType.BANK) {
            return clientMain.getStageHandler().getBankWindow();
        } else if (inventoryType == InventoryType.EQUIPMENT) {
            return clientMain.getStageHandler().getEquipmentWindow();
        } else if (inventoryType == InventoryType.HOT_BAR) {
            return clientMain.getStageHandler().getHotBar();
        }
        throw new RuntimeException("Impossible Case!");
    }
}
