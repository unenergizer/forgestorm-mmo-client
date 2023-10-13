package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.shared.game.world.item.inventory.InventoryType;
import lombok.Getter;

@Getter
public class ItemSlotContainerWindow extends HideableVisWindow {

    private final ItemSlotContainer itemSlotContainer;
    private final InventoryType inventoryType;

    ItemSlotContainerWindow(ClientMain clientMain, String title, int containerSize, InventoryType inventoryType) {
        super(clientMain, title);
        this.itemSlotContainer = new ItemSlotContainer(clientMain, this, containerSize);
        this.inventoryType = inventoryType;
    }
}
