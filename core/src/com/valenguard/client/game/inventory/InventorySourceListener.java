package com.valenguard.client.game.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class InventorySourceListener extends DragAndDrop.Source {

    public InventorySourceListener(Actor inventorySlot) {
        super(inventorySlot);
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        return null;
    }
}
