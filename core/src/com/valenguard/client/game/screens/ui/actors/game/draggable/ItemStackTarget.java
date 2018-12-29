package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.game.inventory.InventoryType;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.inventory.WearableItemStack;
import com.valenguard.client.network.packet.out.InventoryPacketOut;

// TODO implement EQUIPMENT TO EQUIPMENT window actions for ring swapping and that type of thing.
public class ItemStackTarget extends DragAndDrop.Target {

    private final ItemStackSlot targetItemStackSlot;

    private enum WindowMovementInfo {
        FROM_BAG_TO_BAG,
        FROM_BAG_TO_EQUIPMENT,
        FROM_EQUIPMENT_TO_BAG,
        FROM_EQUIPMENT_TO_EQUIPMENT;

        private InventoryType getFromWindow() {
            switch (this) {
                case FROM_BAG_TO_BAG:
                    return InventoryType.BAG;
                case FROM_BAG_TO_EQUIPMENT:
                    return InventoryType.BAG;
                case FROM_EQUIPMENT_TO_BAG:
                    return InventoryType.EQUIPMENT;
                case FROM_EQUIPMENT_TO_EQUIPMENT:
                    return InventoryType.EQUIPMENT;
            }
            throw new RuntimeException("Must implement all cases.");
        }

        private InventoryType getToWindow() {
            switch (this) {
                case FROM_BAG_TO_BAG:
                    return InventoryType.BAG;
                case FROM_BAG_TO_EQUIPMENT:
                    return InventoryType.EQUIPMENT;
                case FROM_EQUIPMENT_TO_BAG:
                    return InventoryType.BAG;
                case FROM_EQUIPMENT_TO_EQUIPMENT:
                    return InventoryType.EQUIPMENT;
            }
            throw new RuntimeException("Must implement all cases.");
        }
    }

    private WindowMovementInfo windowMovementInfo;

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
        if (targetItemStackSlot.getInventoryType() == InventoryType.BAG && sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT && targetItemStack != null) {
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

        // The client is simply picking up and placing down the item in the exact same position.
        if (sourceItemStackSlot.getInventoryIndex() == targetItemStackSlot.getInventoryIndex() &&
                sourceItemStackSlot.getInventoryType() == targetItemStackSlot.getInventoryType()) {
            targetItemStackSlot.setItemStack(sourceItemStack);
            sourceItemStackSlot.setItemStack(targetItemStack);
            return;
        }

        determineWindowMovementInfo(sourceItemStackSlot);

        if (targetItemStack != null) { // Swap (setting back on itself is valid swap)

            swapItemAction(sourceItemStack, targetItemStack, sourceItemStackSlot);

        } else { // No swap just set empty cell

            setItemAction(sourceItemStack, sourceItemStackSlot);

        }
        // todo add another case where we check if the items are the same type and stack them
    }

    private void determineWindowMovementInfo(ItemStackSlot sourceItemStackSlot) {
        if (sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT && targetItemStackSlot.getInventoryType() == InventoryType.BAG) {
            windowMovementInfo = WindowMovementInfo.FROM_EQUIPMENT_TO_BAG;
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.BAG && targetItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            windowMovementInfo = WindowMovementInfo.FROM_BAG_TO_EQUIPMENT;
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.BAG && targetItemStackSlot.getInventoryType() == InventoryType.BAG) {
            windowMovementInfo = WindowMovementInfo.FROM_BAG_TO_BAG;
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT && targetItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            windowMovementInfo = WindowMovementInfo.FROM_EQUIPMENT_TO_EQUIPMENT;
        }
    }

    private void swapItemAction(ItemStack sourceItemStack, ItemStack targetItemStack, ItemStackSlot sourceItemStackSlot) {

        targetItemStackSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.setItemStack(targetItemStack);

        new InventoryPacketOut(new InventoryActions(
                InventoryActions.MOVE,
                windowMovementInfo.getFromWindow(),
                windowMovementInfo.getToWindow(),
                sourceItemStackSlot.getInventoryIndex(),
                targetItemStackSlot.getInventoryIndex()
        )).sendPacket();

        if (windowMovementInfo == WindowMovementInfo.FROM_EQUIPMENT_TO_BAG) {
            if (sourceItemStackSlot.getItemStackType() == ItemStackType.CHEST) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
            } else if (sourceItemStackSlot.getItemStackType() == ItemStackType.HELM) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
            }
        } else if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_EQUIPMENT) {
            setWearableFromSource(sourceItemStack);
        }
    }

    private void setItemAction(ItemStack sourceItemStack, ItemStackSlot sourceItemStackSlot) {

        targetItemStackSlot.setItemStack(sourceItemStack);
        sourceItemStackSlot.deleteStack();

        new InventoryPacketOut(new InventoryActions(
                InventoryActions.MOVE,
                windowMovementInfo.getFromWindow(),
                windowMovementInfo.getToWindow(),
                sourceItemStackSlot.getInventoryIndex(),
                targetItemStackSlot.getInventoryIndex()
        )).sendPacket();

        if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_EQUIPMENT) {
            setWearableFromSource(sourceItemStack);
        } else if (windowMovementInfo == WindowMovementInfo.FROM_EQUIPMENT_TO_BAG) { // Removing armor pieces
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
