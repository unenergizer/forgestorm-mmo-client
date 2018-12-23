package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.inventory.InventoryType;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.inventory.WearableItemStack;

public class ItemStackTarget extends DragAndDrop.Target {

    private ItemStackSlot targetItemStackSlot;

    ItemStackTarget(ItemStackSlot targetItemStackSlot) {
        super(targetItemStackSlot);
        this.targetItemStackSlot = targetItemStackSlot;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (!(source instanceof ItemStackSource)) return false;

        ItemStackSource itemStackSource = (ItemStackSource) source;
        ItemStackSlot sourceItemStackSlot = itemStackSource.getItemStackSlot();
        ItemStack targetItemStack = targetItemStackSlot.getItemStack();
        //  Makes sure the source is a character menu      && Inventory item is not null.
        if (targetItemStackSlot.getInventoryType() == InventoryType.BAG && sourceItemStackSlot.getInventoryType() == InventoryType.CHARACTER && targetItemStack != null) {
            //  Inventory ItemStack ItemStackType  != character menu ItemStackType
            if (targetItemStack.getItemStackType() != sourceItemStackSlot.getItemStackType())
                return false;
        }

        return targetItemStackSlot.getItemStackType() == null ||
                ((ItemStack) payload.getObject()).getItemStackType() == targetItemStackSlot.getItemStackType();
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

        ItemStackSource itemStackSource = (ItemStackSource) source;
        ItemStackSlot sourceItemStackSlot = itemStackSource.getItemStackSlot();

        ItemStack sourceItemStack = (ItemStack) payload.getObject();
        ItemStack targetItemStack = targetItemStackSlot.getItemStack();

        if (targetItemStack != null) { // Swap (setting back on itself is valid swap)

            swapItemAction(sourceItemStack, targetItemStack, sourceItemStackSlot);

        } else { // No swap just set empty cell

            setItemAction(sourceItemStack, sourceItemStackSlot);

        }
        // todo add another case where we check if the items are the same type and stack them
    }

    private void swapItemAction(ItemStack sourceItemStack, ItemStack targetItemStack, ItemStackSlot sourceItemStackSlot) {

        targetItemStackSlot.setStack(sourceItemStack);
        sourceItemStackSlot.setStack(targetItemStack);
        // todo inform the server of our actions

        // From the character menu to the inventory.
        if (sourceItemStackSlot.getInventoryType() == InventoryType.CHARACTER && targetItemStackSlot.getInventoryType() == InventoryType.BAG) {
            if (sourceItemStackSlot.getItemStackType() == ItemStackType.CHEST) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
            } else if (sourceItemStackSlot.getItemStackType() == ItemStackType.HELM) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
            }
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.BAG && targetItemStackSlot.getInventoryType() == InventoryType.CHARACTER) { // From the inventory to the character menu.
            setWearableFromSource(sourceItemStack);
        }
    }

    private void setItemAction(ItemStack sourceItemStack, ItemStackSlot sourceItemStackSlot) {

        targetItemStackSlot.setStack(sourceItemStack);
        sourceItemStackSlot.deleteStack();
        // todo inform the server of our actions

        if (targetItemStackSlot.getInventoryType() == InventoryType.CHARACTER && sourceItemStackSlot.getInventoryType() == InventoryType.BAG) {
            setWearableFromSource(sourceItemStack);
        } else if (targetItemStackSlot.getInventoryType() == InventoryType.BAG && sourceItemStackSlot.getInventoryType() == InventoryType.CHARACTER) { // Removing armor pieces
            if (sourceItemStackSlot.getItemStackType() == ItemStackType.CHEST) {
                EntityManager.getInstance().getPlayerClient().removeArmor();
            } else if (sourceItemStackSlot.getItemStackType() == ItemStackType.HELM) {
                EntityManager.getInstance().getPlayerClient().removeHelm();
            }
        }
    }

    private void setWearableFromSource(ItemStack sourceItemStack) {
        if (targetItemStackSlot.getItemStackType() == ItemStackType.CHEST && sourceItemStack.getItemStackType() == ItemStackType.CHEST) {
            WearableItemStack wearableItemStack = (WearableItemStack) sourceItemStack;
            EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
        } else if (targetItemStackSlot.getItemStackType() == ItemStackType.HELM && sourceItemStack.getItemStackType() == ItemStackType.HELM) {
            WearableItemStack wearableItemStack = (WearableItemStack) sourceItemStack;
            EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
        }
    }
}
