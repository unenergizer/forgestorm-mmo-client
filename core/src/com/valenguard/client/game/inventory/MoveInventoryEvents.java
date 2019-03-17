package com.valenguard.client.game.inventory;

import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemSlotContainer;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackSlot;

import java.util.LinkedList;
import java.util.Queue;

import static com.valenguard.client.util.Log.println;

public class MoveInventoryEvents {

    private final Queue<InventoryMoveData> previousMovements = new LinkedList<InventoryMoveData>();

    public void addPreviousMovement(InventoryMoveData previousMove) {
        previousMovements.add(previousMove);
    }

    public void moveItems(InventoryMoveData inventoryMoveData) {

        // Checking if the previous movement case happened correctly.
        InventoryMoveData previousMove = previousMovements.remove();

        BagWindow bagWindow = ActorUtil.getStageHandler().getBagWindow();
        EquipmentWindow equipmentWindow = ActorUtil.getStageHandler().getEquipmentWindow();

        InventoryType toWindowType = InventoryType.values()[previousMove.getToWindow()];
        if (toWindowType == InventoryType.BAG_1) {
            bagWindow.getItemStackSlot(previousMove.getToPosition()).setMoveSlotLocked(false);
        } else if (toWindowType == InventoryType.EQUIPMENT) {
            equipmentWindow.getItemStackSlot(previousMove.getToPosition()).setMoveSlotLocked(false);
        }

        if (inventoryMoveData.equals(previousMove)) {
            return;
        }

        println(getClass(), "The client is out of sync with the server so we are reajusting to the way the server views things");

        // The client/server are out of sync. Putting them back in sync.

        // previousToPosition -> previousFromPosition <- swap order of previous case.

        moveItemsByInfo(new InventoryMoveData(
                previousMove.getToPosition(),
                previousMove.getFromPosition(),
                previousMove.getFromWindow(),
                previousMove.getToWindow()
        ));      // Undoing the previous move by flipping the order

        moveItemsByInfo(inventoryMoveData); // Now performing the move that the server sees

    }

    private void moveItemsByInfo(InventoryMoveData inventoryMoveData) {

        InventoryType fromWindow = InventoryType.values()[inventoryMoveData.getFromWindow()];
        InventoryType toWindow = InventoryType.values()[inventoryMoveData.getToWindow()];

        WindowMovementInfo windowMovementInfo = determineWindowMovementInfo(fromWindow, toWindow);

        BagWindow bagWindow = ActorUtil.getStageHandler().getBagWindow();
        EquipmentWindow equipmentWindow = ActorUtil.getStageHandler().getEquipmentWindow();

        if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_BAG) {

            moveBetweenSlotContainers(windowMovementInfo, bagWindow, bagWindow, inventoryMoveData);

        } else if (windowMovementInfo == WindowMovementInfo.FROM_EQUIPMENT_TO_EQUIPMENT) {

            moveBetweenSlotContainers(windowMovementInfo, equipmentWindow, equipmentWindow, inventoryMoveData);

        } else if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_EQUIPMENT) {

            moveBetweenSlotContainers(windowMovementInfo, bagWindow, equipmentWindow, inventoryMoveData);

        } else if (windowMovementInfo == WindowMovementInfo.FROM_EQUIPMENT_TO_BAG) {

            moveBetweenSlotContainers(windowMovementInfo, equipmentWindow, bagWindow, inventoryMoveData);

        }
    }

    private void moveBetweenSlotContainers(WindowMovementInfo windowMovementInfo, ItemSlotContainer fromContainer,
                                           ItemSlotContainer toContainer, InventoryMoveData inventoryMoveData) {

        ItemStack sourceItemStack = fromContainer.getItemStack(inventoryMoveData.getFromPosition());
        ItemStack targetItemStack = toContainer.getItemStack(inventoryMoveData.getToPosition());

        // Swapping items because where the item is being moved to there
        // already exist an item there.
        if (targetItemStack != null) {

            swapItems(windowMovementInfo, fromContainer, toContainer, inventoryMoveData);
            toContainer.setItemStack(inventoryMoveData.getToPosition(), sourceItemStack);
            fromContainer.setItemStack(inventoryMoveData.getFromPosition(), targetItemStack);

        } else {

            setItems(windowMovementInfo, fromContainer, toContainer, inventoryMoveData);
            toContainer.setItemStack(inventoryMoveData.getToPosition(), sourceItemStack);
            fromContainer.removeItemStack(inventoryMoveData.getFromPosition());

        }
    }

    /**
     * Called when an {@link ItemStack} is being set on top of another {@link ItemStack}, thus swapping item positions.
     */
    private void swapItems(WindowMovementInfo windowMovementInfo, ItemSlotContainer fromContainer, ItemSlotContainer toContainer, InventoryMoveData inventoryMoveData) {

        ItemStackSlot sourceItemStackSlot = fromContainer.getItemStackSlot(inventoryMoveData.getFromPosition());
        ItemStackSlot targetItemStackSlot = toContainer.getItemStackSlot(inventoryMoveData.getToPosition());

        ItemStack sourceItemStack = fromContainer.getItemStack(inventoryMoveData.getFromPosition());
        ItemStack targetItemStack = toContainer.getItemStack(inventoryMoveData.getToPosition());

        if (windowMovementInfo == WindowMovementInfo.FROM_EQUIPMENT_TO_BAG) { // Removing armor pieces
            if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.CHEST) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
            } else if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.HELM) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
            }
        } else if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_EQUIPMENT) {
            setWearableFromSource(targetItemStackSlot, sourceItemStack);
        }
    }

    /**
     * Called when an {@link ItemStack} gets put into an empty {@link ItemStackSlot}
     */
    private void setItems(WindowMovementInfo windowMovementInfo, ItemSlotContainer fromContainer, ItemSlotContainer toContainer, InventoryMoveData inventoryMoveData) {

        ItemStackSlot sourceItemStackSlot = fromContainer.getItemStackSlot(inventoryMoveData.getFromPosition());
        ItemStackSlot targetItemStackSlot = toContainer.getItemStackSlot(inventoryMoveData.getToPosition());

        ItemStack sourceItemStack = fromContainer.getItemStack(inventoryMoveData.getFromPosition());

        if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_EQUIPMENT) {
            setWearableFromSource(targetItemStackSlot, sourceItemStack);
        } else if (windowMovementInfo == WindowMovementInfo.FROM_EQUIPMENT_TO_BAG) { // Removing armor pieces
            if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.CHEST) {
                EntityManager.getInstance().getPlayerClient().removeArmor();
            } else if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.HELM) {
                EntityManager.getInstance().getPlayerClient().removeHelm();
            }
        }
    }


    /**
     * Attempts to set the players on-screen graphics when equipping an {@link ItemStack}
     *
     * @param targetItemStackSlot The {@link ItemStackSlot} that the {@link ItemStack} is being moved to.
     * @param sourceItemStack     The {@link ItemStack}
     */
    private void setWearableFromSource(ItemStackSlot targetItemStackSlot, ItemStack sourceItemStack) {

        if (targetItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.CHEST && sourceItemStack.getItemStackType() == ItemStackType.CHEST) {
            WearableItemStack wearableItemStack = (WearableItemStack) sourceItemStack;
            EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
        } else if (targetItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.HELM && sourceItemStack.getItemStackType() == ItemStackType.HELM) {
            WearableItemStack wearableItemStack = (WearableItemStack) sourceItemStack;
            EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
        }
    }

    private WindowMovementInfo determineWindowMovementInfo(InventoryType fromWindow, InventoryType toWindow) {
        if (fromWindow == InventoryType.EQUIPMENT && toWindow == InventoryType.BAG_1) {
            return WindowMovementInfo.FROM_EQUIPMENT_TO_BAG;
        } else if (fromWindow == InventoryType.BAG_1 && toWindow == InventoryType.EQUIPMENT) {
            return WindowMovementInfo.FROM_BAG_TO_EQUIPMENT;
        } else if (fromWindow == InventoryType.BAG_1 && toWindow == InventoryType.BAG_1) {
            return WindowMovementInfo.FROM_BAG_TO_BAG;
        } else if (fromWindow == InventoryType.EQUIPMENT && toWindow == InventoryType.EQUIPMENT) {
            return WindowMovementInfo.FROM_EQUIPMENT_TO_EQUIPMENT;
        }
        throw new RuntimeException("Missing MovementInfo case");
    }

}