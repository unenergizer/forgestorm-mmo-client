package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.WearableItemStack;
import com.valenguard.client.game.world.item.inventory.InventoryActions;
import com.valenguard.client.game.world.item.inventory.InventoryMoveData;
import com.valenguard.client.game.world.item.inventory.InventoryMoveType;
import com.valenguard.client.game.world.item.inventory.InventoryMovementUtil;
import com.valenguard.client.game.world.item.inventory.InventoryType;
import com.valenguard.client.network.game.packet.out.InventoryPacketOut;

import static com.valenguard.client.util.Log.println;

// TODO implement EQUIPMENT TO EQUIPMENT window actions for ring swapping and that type of thing.
public class ItemStackTarget extends DragAndDrop.Target {

    /**
     * The slot that at {@link ItemStack} is being dragged too.
     */
    private final ItemStackSlot itemStackTargetSlot;

    /**
     * Movement identification determined when an {@link ItemStack} gets dropped.
     */
    private InventoryMoveType inventoryMoveType;

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

        println(getClass(), "dropping item");


        ItemStack sourceItemStack = (ItemStack) payload.getObject();
        ItemStack targetItemStack = itemStackTargetSlot.getItemStack();

        ItemStackSource itemStackSource = (ItemStackSource) source;
        ItemStackSlot sourceItemStackSlot = itemStackSource.getItemStackSlot();

        if (sourceItemStackSlot.isTradeSlotLocked() || itemStackTargetSlot.isTradeSlotLocked() || sourceItemStackSlot.isMoveSlotLocked()) {
            sourceItemStackSlot.setItemStack(sourceItemStack);
            println(getClass(), "trade slot locked");
            return;
        }

        // The client is simply picking up and placing down the item in the exact same position.
        if (sourceItemStackSlot.getInventoryIndex() == itemStackTargetSlot.getInventoryIndex() &&
                sourceItemStackSlot.getInventoryType() == itemStackTargetSlot.getInventoryType()) {
            itemStackTargetSlot.setItemStack(sourceItemStack);
            sourceItemStackSlot.setItemStack(targetItemStack);
            println(getClass(), "Picking up and placing back down");
            return;
        }

        inventoryMoveType = InventoryMovementUtil.getWindowMovementInfo(sourceItemStackSlot.getInventoryType(), itemStackTargetSlot.getInventoryType());

        itemStackTargetSlot.setMoveSlotLocked(true);

        println(getClass(), "Sending movement packet");
        println(getClass(), "fromWindow  = " + inventoryMoveType.getFromWindow());
        println(getClass(), "toWindow  = " + inventoryMoveType.getToWindow());
        new InventoryPacketOut(new InventoryActions(
                InventoryActions.ActionType.MOVE,
                inventoryMoveType.getFromWindow(),
                inventoryMoveType.getToWindow(),
                sourceItemStackSlot.getInventoryIndex(),
                itemStackTargetSlot.getInventoryIndex()
        )).sendPacket();

        Valenguard.getInstance().getMoveInventoryEvents().addPreviousMovement(
                new InventoryMoveData(
                        sourceItemStackSlot.getInventoryIndex(),
                        itemStackTargetSlot.getInventoryIndex(),
                        inventoryMoveType.getFromWindow().getInventoryTypeIndex(),
                        inventoryMoveType.getToWindow().getInventoryTypeIndex()
                ));

        changeEquipment(sourceItemStackSlot);

        if (targetItemStack != null) {
            // Swap (setting back on itself is valid swap)
            swapItemAction(sourceItemStack, targetItemStack, sourceItemStackSlot);
        } else {
            // No swap just set empty cell
            setItemAction(sourceItemStack, sourceItemStackSlot);
        }
    }

    private void changeEquipment(ItemStackSlot sourceItemStackSlot) {
        println(getClass(), "changeEquipment()");
        if (itemStackTargetSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            equipItem(itemStackTargetSlot, sourceItemStackSlot.getItemStack());
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            println(getClass(), "From equipment to other inventory");
            if (itemStackTargetSlot.getItemStack() != null) { // Swapping
                equipItem(sourceItemStackSlot, itemStackTargetSlot.getItemStack());
            } else { // Removing equipment
                println(getClass(), "Removing equipment");
                if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.CHEST) {
                    println(getClass(), "Removing chest");
                    EntityManager.getInstance().getPlayerClient().removeArmor();
                } else if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.HELM) {
                    println(getClass(), "Removing helm");
                    EntityManager.getInstance().getPlayerClient().removeHelm();
                }
            }
        }

    }

    private void equipItem(ItemStackSlot itemStackSlot, ItemStack equipItem) {
        if (itemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.CHEST) {
            WearableItemStack wearableItemStack = (WearableItemStack) equipItem;
            EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
        } else if (itemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.HELM) {
            WearableItemStack wearableItemStack = (WearableItemStack) equipItem;
            EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
        }
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
