package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

import lombok.Getter;

@Getter
public class ItemSlotContainerWindow extends HideableVisWindow {

    private final ItemSlotContainer itemSlotContainer;

    ItemSlotContainerWindow(String title, int containerSize) {
        super(title);
        this.itemSlotContainer = new ItemSlotContainer(this, containerSize);
    }

}
