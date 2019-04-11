package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.inventory.InventoryActions;
import com.valenguard.client.game.world.item.inventory.InventoryMoveData;
import com.valenguard.client.game.world.item.inventory.InventoryMoveType;
import com.valenguard.client.game.world.item.inventory.InventoryMovementUtil;
import com.valenguard.client.game.world.item.inventory.InventoryType;
import com.valenguard.client.network.game.packet.out.InventoryPacketOut;

import static com.valenguard.client.util.Log.println;

public class ItemStackTarget extends DragAndDrop.Target {

    /**
     * The slot that at {@link ItemStack} is being dragged too.
     */
    private final ItemStackSlot itemStackTargetSlot;

    /**
     * Movement identification determined when an {@link ItemStack} gets dropped.
     */
    private InventoryMoveType inventoryMoveType;

    private static final boolean PRINT_DEBUG = false;

    ItemStackTarget(ItemStackSlot itemStackTargetSlot) {
        super(itemStackTargetSlot);
        this.itemStackTargetSlot = itemStackTargetSlot;
    }

    /**
     * Called when the payload is dragged over the target. The coordinates are in the target's local coordinate system.
     *
     * @return True if this is a valid target for the payload.
     */
    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (!(source instanceof ItemStackSource)) return false;

        ItemStackSource itemStackSource = (ItemStackSource) source;
        ItemStackSlot sourceItemStackSlot = itemStackSource.getItemStackSlot();

        if (sourceItemStackSlot.isTradeSlotLocked() || itemStackTargetSlot.isTradeSlotLocked())
            return false;

        return checkCanEquip(sourceItemStackSlot);
    }

    private boolean checkCanEquip(ItemStackSlot sourceItemStackSlot) {

        ItemStack targetItemStack = itemStackTargetSlot.getItemStack();
        ItemStack sourceItemStack = sourceItemStackSlot.getItemStack();

        if (sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT && itemStackTargetSlot.getInventoryType() != InventoryType.EQUIPMENT) {
            if (targetItemStack != null) {
                return sourceItemStackSlot.isAcceptedItemStackType(targetItemStack);
            }
        }

        if (itemStackTargetSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            return itemStackTargetSlot.isAcceptedItemStackType(sourceItemStack);
        }

        return true;
    }

    /**
     * Called when the payload is dropped on the target. The coordinates are in the target's local coordinate system.
     */
    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

        ItemStack sourceItemStack = (ItemStack) payload.getObject();
        ItemStack targetItemStack = itemStackTargetSlot.getItemStack();

        ItemStackSource itemStackSource = (ItemStackSource) source;
        ItemStackSlot sourceItemStackSlot = itemStackSource.getItemStackSlot();

        if (sourceItemStackSlot.isTradeSlotLocked() || itemStackTargetSlot.isTradeSlotLocked()
                || sourceItemStackSlot.isMoveSlotLocked() || Valenguard.getInstance().getMoveInventoryEvents().isSyncingInventory()) {
            sourceItemStackSlot.setItemStack(sourceItemStack);
            println(getClass(), "cannot move item at this time", false, PRINT_DEBUG);
            return;
        }

        // The client is simply picking up and placing down the item in the exact same position.
        if (sourceItemStackSlot.getSlotIndex() == itemStackTargetSlot.getSlotIndex() &&
                sourceItemStackSlot.getInventoryType() == itemStackTargetSlot.getInventoryType()) {
            itemStackTargetSlot.setItemStack(sourceItemStack);
            sourceItemStackSlot.setItemStack(targetItemStack);
            println(getClass(), "Picking up and placing back down", false, PRINT_DEBUG);
            return;
        }

        boolean isStack = false;
        if (targetItemStack != null && sourceItemStack.getStackable() > 0 && targetItemStack.getStackable() > 0
                && sourceItemStack.getItemStackType() == targetItemStack.getItemStackType()) {

            // TODO: Check max size if (true) -?> return
            isStack = true;
        }

        inventoryMoveType = InventoryMovementUtil.getWindowMovementInfo(sourceItemStackSlot.getInventoryType(), itemStackTargetSlot.getInventoryType());

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
                stackItemAction(sourceItemStack, targetItemStack, sourceItemStackSlot);
            } else {
                swapItemAction(sourceItemStack, targetItemStack, sourceItemStackSlot);
            }

        } else {
            // No swap just set empty cell
            setItemAction(sourceItemStack, sourceItemStackSlot);
        }
    }


    private void stackItemAction(ItemStack sourceItemStack, ItemStack targetItemStack, ItemStackSlot sourceItemStackSlot) {
        sourceItemStack.setAmount(sourceItemStack.getAmount() + targetItemStack.getAmount());

        itemStackTargetSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.deleteStack();
    }

    /**
     * Called when an {@link ItemStack} is being set on top of another {@link ItemStack}, thus swapping item positions.
     *
     * @param sourceItemStack     The {@link ItemStack} that was picked up and will be dropped onto a TargetSlot.
     * @param targetItemStack     The {@link ItemStack} that was resting and then had an {@link ItemStack} dropped on top of it, forcing a item swap.
     * @param sourceItemStackSlot The {@link ItemStackSlot} that the {@link ItemStack} was picked up from.
     */
    private void swapItemAction(ItemStack sourceItemStack, ItemStack targetItemStack, ItemStackSlot sourceItemStackSlot) {
        itemStackTargetSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.setItemStack(targetItemStack);
    }

    /**
     * Called when an {@link ItemStack} gets put into an empty {@link ItemStackSlot}
     *
     * @param sourceItemStack     The {@link ItemStack} that was picked up and has been dropped into a {@link ItemStackSlot}
     * @param sourceItemStackSlot The {@link ItemStackSlot} that the {@link ItemStack} was picked up from.
     */
    private void setItemAction(ItemStack sourceItemStack, ItemStackSlot sourceItemStackSlot) {
        itemStackTargetSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.deleteStack();
    }
}
