package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.valenguard.client.game.inventory.ItemStack;

public class ItemStackTarget extends DragAndDrop.Target {

    private ItemStackSlot targetItemStackSlot;

    ItemStackTarget(ItemStackSlot targetItemStackSlot) {
        super(targetItemStackSlot);
        this.targetItemStackSlot = targetItemStackSlot;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (!(source instanceof ItemStackSource)) return false;
        return targetItemStackSlot.getItemStackType() == null || ((ItemStack) payload.getObject()).getItemStackType() == targetItemStackSlot.getItemStackType();
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

        ItemStackSource itemStackSource = (ItemStackSource) source;
        ItemStackSlot sourceItemStackSlot = itemStackSource.getItemStackSlot();

        ItemStack sourceItemStack = (ItemStack) payload.getObject();
        ItemStack targetItemStack = targetItemStackSlot.getItemStack();

        if (targetItemStack != null) { // Swap (setting back on itself is valid swap)

            targetItemStackSlot.setStack(sourceItemStack);
            sourceItemStackSlot.setStack(targetItemStack);
            // todo inform the server of our actions

        } else {

            targetItemStackSlot.setStack(sourceItemStack);
            sourceItemStackSlot.deleteStack();
            // todo inform the server of our actions

        }
        // todo add another case where we check if the items are the same type and stack them

    }
}
