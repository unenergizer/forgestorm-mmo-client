package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.item.inventory.InventoryType;

import lombok.Getter;

@Getter
public class ItemSlotContainerWindow extends HideableVisWindow {

    private final ItemSlotContainer itemSlotContainer;
    private final InventoryType inventoryType;

    ItemSlotContainerWindow(String title, int containerSize, InventoryType inventoryType) {
        super(title);
        this.itemSlotContainer = new ItemSlotContainer(this, containerSize);
        this.inventoryType = inventoryType;
    }
}
