package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class ItemSlotContainer extends HideableVisWindow  {

    final ItemStackSlot[] itemStackSlots;

    ItemSlotContainer(String title, int containerSize) {
        super(title);
        itemStackSlots = new ItemStackSlot[containerSize];
    }

    public ItemStack getItemStack(byte slotIndex) {
        return itemStackSlots[slotIndex].getItemStack();
    }

    /**
     * Adds an {@link ItemStack} to an empty slot.
     *
     * @param itemStack The {@link ItemStack} to add to the players inventory.
     */
    public void addItemStack(ItemStack itemStack) {
        for (byte i = 0; i < ClientConstants.BAG_SIZE; i++) {

            // Find empty slot
            if (itemStackSlots[i].getItemStack() != null) continue;

            // Empty slot found. Placing item
            itemStackSlots[i].setItemStack(itemStack);
            return;
        }
    }

    public void setItemStack(byte slotIndex, ItemStack itemStack) {

        itemStackSlots[slotIndex].setItemStack(itemStack);

    }

    public void removeItemStack(byte slotIndex) {
        itemStackSlots[slotIndex].setEmptyCellImage();
        itemStackSlots[slotIndex].deleteStack();
    }

    public ItemStackSlot getItemStackSlot(byte slotIndex) {
        return itemStackSlots[slotIndex];
    }
}
