package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.world.item.ItemStack;

public class ItemSlotContainer extends HideableVisWindow {

    final ItemStackSlot[] itemStackSlots;
    private final int containerSize;

    ItemSlotContainer(String title, int containerSize) {
        super(title);
        itemStackSlots = new ItemStackSlot[containerSize];
        this.containerSize = containerSize;
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
        for (byte i = 0; i < containerSize; i++) {

            // Find empty slot
            if (itemStackSlots[i].getItemStack() != null) continue;

            // Empty slot found. Placing item
            itemStackSlots[i].setItemStack(itemStack);
            return;
        }
    }

    /**
     * This will test to see if we have an empty slot in the target container.
     *
     * @param itemStack The {@link ItemStack} that we want to move.
     * @return An {@link ItemStackSlot} if one is free or if the item we are moving can
     * be stacked into a slot that contains that same {@link ItemStack}.
     */
    public ItemStackSlot getFreeItemStackSlot(ItemStack itemStack) {
        for (byte i = 0; i < containerSize; i++) {

            // Test for empty slot
            if (itemStackSlots[i].getItemStack() == null) return itemStackSlots[i];

            ItemStack targetItemStack = itemStackSlots[i].getItemStack();
            boolean bothStackable = itemStack.getStackable() > 1 && targetItemStack.getStackable() > 1;
            boolean bothSameID = itemStack.getItemId() == targetItemStack.getItemId();
            // TODO: Make then test for the below line.
            //  boolean notOverMax = itemStack.getAmount() + targetItemStack.getAmount() < itemStack.getMaxStackSize();

            // Test for stackable slot
            if (bothStackable && bothSameID) {
                return itemStackSlots[i];
            }
        }
        return null;
    }

    boolean isInventoryFull() {
        boolean foundFreeSlot = false;
        for (byte i = 0; i < containerSize; i++) {
            if (itemStackSlots[i].getItemStack() == null) {
                // Found an empty slot
                foundFreeSlot = true;
                break;
            } else if (itemStackSlots[i].getItemStack().getStackable() > 1) {
                // Found a stackable slot
                foundFreeSlot = true;
                break;
            }
        }
        return !foundFreeSlot;
    }

    public void setItemStack(byte slotIndex, ItemStack itemStack) {
        itemStackSlots[slotIndex].setItemStack(itemStack);
    }

    public void removeItemStack(byte slotIndex) {
        itemStackSlots[slotIndex].setEmptyCellImage();
        itemStackSlots[slotIndex].resetItemStackSlot();
        itemStackSlots[slotIndex].setMoveSlotLocked(false);
    }

    public ItemStackSlot getItemStackSlot(byte slotIndex) {
        return itemStackSlots[slotIndex];
    }

    public void resetItemSlotContainer() {
        for (ItemStackSlot itemStackSlot : itemStackSlots) {
            itemStackSlot.resetItemStackSlot();
        }
    }
}
