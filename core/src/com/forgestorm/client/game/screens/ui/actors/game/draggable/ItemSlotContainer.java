package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.game.world.item.inventory.InventoryType;

import lombok.Getter;

public class ItemSlotContainer {

    @Getter
    private final Actor parentActor;
    final ItemStackSlot[] itemStackSlots;
    private final int containerSize;

    ItemSlotContainer(Actor parentActor, int containerSize) {
        this.parentActor = parentActor;
        itemStackSlots = new ItemStackSlot[containerSize];
        this.containerSize = containerSize;
    }

    ItemStack getItemStack(byte slotIndex) {
        return itemStackSlots[slotIndex].getItemStack();
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

    boolean isInventoryFull(ItemStack itemStack) {
        return getFreeItemStackSlot(itemStack) == null;
    }

    public void setItemStack(byte slotIndex, ItemStack itemStack) {
        itemStackSlots[slotIndex].setItemStack(itemStack);
    }

    public void removeItemStack(byte slotIndex) {
        itemStackSlots[slotIndex].setEmptyCellImage();
        itemStackSlots[slotIndex].resetItemStackSlot();
        itemStackSlots[slotIndex].setMoveSlotLocked(false);
    }

    ItemStackSlot getItemStackSlot(byte slotIndex) {
        return itemStackSlots[slotIndex];
    }

    public void resetItemSlotContainer() {
        for (ItemStackSlot itemStackSlot : itemStackSlots) {
            itemStackSlot.resetItemStackSlot();
        }
    }

    void swapInventories(ItemStack sourceItemStack, ItemStackSlot sourceSlot, ItemSlotContainer itemSlotContainer) {
        if (itemSlotContainer.isInventoryFull(sourceItemStack)) {
            ClientMain.getInstance().getStageHandler().getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[RED]Cannot transfer item because the inventory is full!");
            return;
        }

        ItemStackSlot targetSlot = itemSlotContainer.getFreeItemStackSlot(sourceItemStack);

        new InventoryMoveActions().moveItems(sourceSlot, targetSlot, sourceItemStack, targetSlot.getItemStack());
        ClientMain.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), sourceItemStack);

        sourceSlot.setEmptyCellImage();
    }

    void magicItemInteract(ItemStackSlot sourceSlot, ItemStack itemStack) {
        if (sourceSlot.getInventoryType() == InventoryType.BANK) return;
        ClientMain.getInstance().getAbilityManager().toggleAbility(sourceSlot, itemStack);
    }
}
