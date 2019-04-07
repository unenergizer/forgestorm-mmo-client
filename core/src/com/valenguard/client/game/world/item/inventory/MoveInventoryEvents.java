package com.valenguard.client.game.world.item.inventory;

import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemSlotContainer;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackSlot;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.WearableItemStack;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

// TODO: dropping items is not handled. The dropping of items would have to behave like a
// TODO: movement or it would have to wait on a response from the server.
public class MoveInventoryEvents {

    private static final boolean PRINT_DEBUG = true;

    private final Queue<InventoryMoveData> previousMovements = new LinkedList<InventoryMoveData>();

    @Getter
    private boolean synchingInventory = false;

    public void addPreviousMovement(InventoryMoveData previousMove) {
        previousMovements.add(previousMove);
    }

    public void receivedNonMoveRequest() {
        if (previousMovements.isEmpty() || synchingInventory) return;

        println(getClass(), "Rewinding the inventory.");

        // Setting the inventory back to it's previous state!
        for (InventoryMoveData previousMove : previousMovements) {
            byte fromPosition = previousMove.getFromPosition();
            byte toPosition = previousMove.getToPosition();

            ItemSlotContainer fromWindow = getItemSlotContainer(previousMove.getFromWindow());
            ItemSlotContainer toWindow = getItemSlotContainer(previousMove.getToWindow());

            ItemStack fromItemStack = fromWindow.getItemStack(fromPosition);
            ItemStack toItemStack = toWindow.getItemStack(toPosition);

            changeEquipment(toWindow.getItemStackSlot(toPosition), fromWindow.getItemStackSlot(fromPosition));

            // Flipping the order
            fromWindow.setItemStack(fromPosition, toItemStack);
            toWindow.setItemStack(toPosition, fromItemStack);

            // Unlocking the movement but sync locking instead
            toWindow.getItemStackSlot(toPosition).setMoveSlotLocked(false);

        }

        // Locking the inventory!
        synchingInventory = true;

        // 1. walk back all of are moves...
        // 2. lock the inventory
        // 3. wait for server responses
        // unlock inventory
    }

    private ItemSlotContainer getItemSlotContainer(byte inventoryByte) {
        InventoryType inventoryType = InventoryType.values()[inventoryByte];
        if (inventoryType == InventoryType.BAG_1) {
            return ActorUtil.getStageHandler().getBagWindow();
        } else if (inventoryType == InventoryType.BANK) {
            return ActorUtil.getStageHandler().getBankWindow();
        } else if (inventoryType == InventoryType.EQUIPMENT) {
            return ActorUtil.getStageHandler().getEquipmentWindow();
        }
        throw new RuntimeException("Impossible Case!");
    }


    public void moveItems(InventoryMoveData inventoryMoveData) {
        InventoryMoveData previousMove = previousMovements.remove();

        ItemSlotContainer fromWindow = getItemSlotContainer(previousMove.getFromWindow());
        ItemSlotContainer toWindow = getItemSlotContainer(previousMove.getToWindow());

        if (synchingInventory) {

            byte fromPosition = previousMove.getFromPosition();
            byte toPosition = previousMove.getToPosition();

            ItemStack fromItemStack = fromWindow.getItemStack(fromPosition);
            ItemStack toItemStack = toWindow.getItemStack(toPosition);

            if (fromPosition == toPosition && previousMove.getFromWindow() == previousMove.getToWindow()) {
                // The client must have at some point sent a move that was invalid from the perspective of the server.
                return;
            }

            // Performing the inventory move that the server says to perform.
            changeEquipment(toWindow.getItemStackSlot(toPosition), fromWindow.getItemStackSlot(fromPosition));

            toWindow.setItemStack(toPosition, fromItemStack);
            fromWindow.setItemStack(fromPosition, toItemStack);

            // Reached the end of the movements and now the client is in alignment with the server.
            if (previousMovements.isEmpty()) {
                synchingInventory = false;
            }

        } else {
            // The server and the client are not synching so it should
            // be the case that the responses match
            if (!previousMove.equals(inventoryMoveData)) {

                println(getClass(), "Move request response was not the same as the servers.", true);
                println(getClass(), "-------SERVER-------", true);
                println(getClass(), "fromPosition = " + inventoryMoveData.getFromPosition(), true);
                println(getClass(), "toPosition = " + inventoryMoveData.getToPosition(), true);
                println(getClass(), "fromWindow = " + inventoryMoveData.getFromWindow(), true);
                println(getClass(), "toWindow = " + inventoryMoveData.getToWindow(), true);
                println(getClass(), "-------CLIENT-------", true);
                println(getClass(), "fromPosition = " + previousMove.getFromPosition(), true);
                println(getClass(), "toPosition = " + previousMove.getToPosition(), true);
                println(getClass(), "fromWindow = " + previousMove.getFromWindow(), true);
                println(getClass(), "toWindow = " + previousMove.getToWindow(), true);

            } else {
                toWindow.getItemStackSlot(previousMove.getToPosition()).setMoveSlotLocked(false); // Unlocking the inventory position!
            }
        }

    }

    public void changeEquipment(ItemStackSlot itemStackTargetSlot, ItemStackSlot sourceItemStackSlot) {
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
}
