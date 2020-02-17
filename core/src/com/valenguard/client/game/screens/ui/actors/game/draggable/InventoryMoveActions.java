package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.inventory.InventoryActions;
import com.valenguard.client.game.world.item.inventory.InventoryMoveData;
import com.valenguard.client.game.world.item.inventory.InventoryMoveType;
import com.valenguard.client.game.world.item.inventory.InventoryMovementUtil;
import com.valenguard.client.network.game.packet.out.InventoryPacketOut;

import static com.valenguard.client.util.Log.println;

public class InventoryMoveActions {

    private static final boolean PRINT_DEBUG = false;

    public void moveItems(ItemStackSlot sourceItemStackSlot, ItemStackSlot itemStackTargetSlot,
                          ItemStack sourceItemStack, ItemStack targetItemStack) {

        // If the target slot is null, do not move items..
        if (itemStackTargetSlot == null) return;
        
        if (sourceItemStackSlot.isTradeSlotLocked() || itemStackTargetSlot.isTradeSlotLocked()
                || sourceItemStackSlot.isMoveSlotLocked() || Valenguard.getInstance().getMoveInventoryEvents().isSyncingInventory()) {
            return;
        }

        boolean isStack = false;
        if (targetItemStack != null && sourceItemStack.getStackable() > 1 && targetItemStack.getStackable() > 1
                && sourceItemStack.getItemStackType() == targetItemStack.getItemStackType()) {

            // TODO: Check max size if (true) -?> return
            isStack = true;
        }

        InventoryMoveType inventoryMoveType = InventoryMovementUtil.getWindowMovementInfo(sourceItemStackSlot.getInventoryType(), itemStackTargetSlot.getInventoryType());

        itemStackTargetSlot.setMoveSlotLocked(true);

        println(getClass(), "Sending movement packet", false, PRINT_DEBUG);
        println(getClass(), "fromWindow  = " + inventoryMoveType.getFromWindow(), false, PRINT_DEBUG);
        println(getClass(), "toWindow  = " + inventoryMoveType.getToWindow(), false, PRINT_DEBUG);
        println(getClass(), "fromPosition  = " + sourceItemStackSlot.getSlotIndex(), false, PRINT_DEBUG);
        println(getClass(), "toPosition  = " + itemStackTargetSlot.getSlotIndex(), false, PRINT_DEBUG);
        new InventoryPacketOut(new InventoryActions(
                InventoryActions.ActionType.MOVE,
                inventoryMoveType.getFromWindow(),
                inventoryMoveType.getToWindow(),
                sourceItemStackSlot.getSlotIndex(),
                itemStackTargetSlot.getSlotIndex()
        )).sendPacket();

        Valenguard.getInstance().getMoveInventoryEvents().addPreviousMovement(
                new InventoryMoveData(
                        sourceItemStackSlot.getSlotIndex(),
                        itemStackTargetSlot.getSlotIndex(),
                        inventoryMoveType.getFromWindow().getInventoryTypeIndex(),
                        inventoryMoveType.getToWindow().getInventoryTypeIndex(),
                        isStack,
                        sourceItemStack.getAmount()
                ));

        Valenguard.getInstance().getMoveInventoryEvents().changeEquipment(itemStackTargetSlot, sourceItemStackSlot);

        if (targetItemStack != null) {

            if (isStack) {
                stackItemAction(itemStackTargetSlot, sourceItemStack, targetItemStack, sourceItemStackSlot);
            } else {
                swapItemAction(itemStackTargetSlot, sourceItemStack, targetItemStack, sourceItemStackSlot);
            }

        } else {
            // No swap just set empty cell
            setItemAction(itemStackTargetSlot, sourceItemStack, sourceItemStackSlot);
        }
    }

    private void stackItemAction(ItemStackSlot itemStackTargetSlot, ItemStack sourceItemStack, ItemStack targetItemStack, ItemStackSlot sourceItemStackSlot) {
        sourceItemStack.setAmount(sourceItemStack.getAmount() + targetItemStack.getAmount());

        itemStackTargetSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.resetItemStackSlot();
    }

    /**
     * Called when an {@link ItemStack} is being set on top of another {@link ItemStack}, thus swapping item positions.
     *
     * @param sourceItemStack     The {@link ItemStack} that was picked up and will be dropped onto a TargetSlot.
     * @param targetItemStack     The {@link ItemStack} that was resting and then had an {@link ItemStack} dropped on top of it, forcing a item swap.
     * @param sourceItemStackSlot The {@link ItemStackSlot} that the {@link ItemStack} was picked up from.
     */
    private void swapItemAction(ItemStackSlot itemStackTargetSlot, ItemStack sourceItemStack, ItemStack targetItemStack, ItemStackSlot sourceItemStackSlot) {
        itemStackTargetSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.setItemStack(targetItemStack);
    }

    /**
     * Called when an {@link ItemStack} gets put into an empty {@link ItemStackSlot}
     *
     * @param sourceItemStack     The {@link ItemStack} that was picked up and has been dropped into a {@link ItemStackSlot}
     * @param sourceItemStackSlot The {@link ItemStackSlot} that the {@link ItemStack} was picked up from.
     */
    private void setItemAction(ItemStackSlot itemStackTargetSlot, ItemStack sourceItemStack, ItemStackSlot sourceItemStackSlot) {
        itemStackTargetSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.resetItemStackSlot();
    }
}
