package com.valenguard.client.game.screens.ui.actors.game.inventory;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.valenguard.client.game.inventory.ItemStack;

public class InventoryTarget extends DragAndDrop.Target {

    private InventorySlot targetInventorySlot;

    private byte inventoryIndex;

    public InventoryTarget(InventorySlot targetInventorySlot, byte inventoryIndex) {
        super(targetInventorySlot);
        this.targetInventorySlot = targetInventorySlot;
        this.inventoryIndex = inventoryIndex;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (!(source instanceof InventorySource)) return false;
        return !((InventorySource) source).getInventorySlot().equals(targetInventorySlot);
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

        InventorySource inventorySource = (InventorySource) source;
        InventorySlot sourceInventorySlot = inventorySource.getInventorySlot();

        ItemStack sourceItemStack = (ItemStack) payload.getObject();
        ItemStack targetItemStack = targetInventorySlot.getItemStack();

        if (targetItemStack != null) { // Swap

            targetInventorySlot.setStack(sourceItemStack);
            sourceInventorySlot.setStack(targetItemStack);
            // todo inform the server of our actions

        } else {

            targetInventorySlot.setStack(sourceItemStack);
            sourceInventorySlot.deleteStack();
            // todo inform the server of our actions

        }
        // todo add another case where we check if the items are the same type and stack them

    }
}
