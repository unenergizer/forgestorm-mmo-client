package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.game.inventory.InventoryMoveData;
import com.valenguard.client.game.inventory.InventoryType;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.inventory.WearableItemStack;
import com.valenguard.client.network.game.packet.out.InventoryPacketOut;

// TODO implement EQUIPMENT TO EQUIPMENT window actions for ring swapping and that type of thing.
public class ItemStackTarget extends DragAndDrop.Target {

    /**
     * The slot that at {@link ItemStack} is being dragged too.
     */
    private final ItemStackSlot itemStackTargetSlot;

    /**
     * Movement identification determined when an {@link ItemStack} gets dropped.
     */
    private WindowMovementInfo windowMovementInfo;

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
        ItemStack targetItemStack = itemStackTargetSlot.getItemStack();

        if (sourceItemStackSlot.isTradeSlotLocked() || itemStackTargetSlot.isTradeSlotLocked())
            return false;

        // Being dragged from equipment to bag and swapping.
        if (itemStackTargetSlot.getInventoryType() == InventoryType.BAG_1
                && sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT
                && targetItemStack != null
                && sourceItemStackSlot.getItemStack() != null) {
            // The source is the equipment inventory. The item being placed into their
            // equipment inventory is the target's (the bag) ItemStack.
            return sourceItemStackSlot.isAcceptedItemStackType(targetItemStack);
        }

        // Being dragged equipment to the bag.
        if (itemStackTargetSlot.getInventoryType() == InventoryType.BAG_1
                && sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT
                && targetItemStack != null) {
            return itemStackTargetSlot.isAcceptedItemStackType(targetItemStack);
        }

        return itemStackTargetSlot.getAcceptedItemStackTypes() == null || itemStackTargetSlot.isAcceptedItemStackType((ItemStack) payload.getObject());
    }

    /**
     * Called when the payload is dropped on the target. The coordinates are in the target's local coordinate system.
     */
    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {

        ItemStackSource itemStackSource = (ItemStackSource) source;
        ItemStackSlot sourceItemStackSlot = itemStackSource.getItemStackSlot();

        if (sourceItemStackSlot.isTradeSlotLocked() || itemStackTargetSlot.isTradeSlotLocked() || sourceItemStackSlot.isMoveSlotLocked())
            return;

        ItemStack sourceItemStack = (ItemStack) payload.getObject();
        ItemStack targetItemStack = itemStackTargetSlot.getItemStack();

        // The client is simply picking up and placing down the item in the exact same position.
        if (sourceItemStackSlot.getInventoryIndex() == itemStackTargetSlot.getInventoryIndex() &&
                sourceItemStackSlot.getInventoryType() == itemStackTargetSlot.getInventoryType()) {
            itemStackTargetSlot.setItemStack(sourceItemStack);
            sourceItemStackSlot.setItemStack(targetItemStack);
            return;
        }

        determineWindowMovementInfo(sourceItemStackSlot);

        itemStackTargetSlot.setMoveSlotLocked(true);

        if (targetItemStack != null) {
            // Swap (setting back on itself is valid swap)
            swapItemAction(sourceItemStack, targetItemStack, sourceItemStackSlot);
        } else {
            // No swap just set empty cell
            setItemAction(sourceItemStack, sourceItemStackSlot);
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

        new InventoryPacketOut(new InventoryActions(
                InventoryActions.MOVE,
                windowMovementInfo.getFromWindow(),
                windowMovementInfo.getToWindow(),
                sourceItemStackSlot.getInventoryIndex(),
                itemStackTargetSlot.getInventoryIndex()
        )).sendPacket();

        Valenguard.getInstance().getMoveInventoryEvents().addPreviousMovement(
                new InventoryMoveData(
                        sourceItemStackSlot.getInventoryIndex(),
                        itemStackTargetSlot.getInventoryIndex(),
                        windowMovementInfo.getFromWindow().getInventoryTypeIndex(),
                        windowMovementInfo.getToWindow().getInventoryTypeIndex()
                ));

        if (windowMovementInfo == WindowMovementInfo.FROM_EQUIPMENT_TO_BAG) { // Removing armor pieces
            if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.CHEST) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
            } else if (sourceItemStackSlot.getAcceptedItemStackTypes()[0] == ItemStackType.HELM) {
                WearableItemStack wearableItemStack = (WearableItemStack) targetItemStack;
                EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
            }
        } else if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_EQUIPMENT) {
            setWearableFromSource(sourceItemStack);
        }
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

        new InventoryPacketOut(new InventoryActions(
                InventoryActions.MOVE,
                windowMovementInfo.getFromWindow(),
                windowMovementInfo.getToWindow(),
                sourceItemStackSlot.getInventoryIndex(),
                itemStackTargetSlot.getInventoryIndex()
        )).sendPacket();

        Valenguard.getInstance().getMoveInventoryEvents().addPreviousMovement(
                new InventoryMoveData(
                        sourceItemStackSlot.getInventoryIndex(),
                        itemStackTargetSlot.getInventoryIndex(),
                        windowMovementInfo.getFromWindow().getInventoryTypeIndex(),
                        windowMovementInfo.getToWindow().getInventoryTypeIndex()
                ));

        if (windowMovementInfo == WindowMovementInfo.FROM_BAG_TO_EQUIPMENT) {
            setWearableFromSource(sourceItemStack);
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
     * @param sourceItemStack The {@link ItemStack}
     */
    private void setWearableFromSource(ItemStack sourceItemStack) {
        if (itemStackTargetSlot.getAcceptedItemStackTypes()[0] == ItemStackType.CHEST && sourceItemStack.getItemStackType() == ItemStackType.CHEST) {
            WearableItemStack wearableItemStack = (WearableItemStack) sourceItemStack;
            EntityManager.getInstance().getPlayerClient().setArmor(wearableItemStack.getTextureId());
        } else if (itemStackTargetSlot.getAcceptedItemStackTypes()[0] == ItemStackType.HELM && sourceItemStack.getItemStackType() == ItemStackType.HELM) {
            WearableItemStack wearableItemStack = (WearableItemStack) sourceItemStack;
            EntityManager.getInstance().getPlayerClient().setHelm(wearableItemStack.getTextureId());
        }
    }

    /**
     * Determines how an {@link ItemStack} is being moved.
     *
     * @param sourceItemStackSlot The source location that the {@link ItemStack} being moved came from.
     */
    private void determineWindowMovementInfo(ItemStackSlot sourceItemStackSlot) {
        if (sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT && itemStackTargetSlot.getInventoryType() == InventoryType.BAG_1) {
            windowMovementInfo = WindowMovementInfo.FROM_EQUIPMENT_TO_BAG;
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.BAG_1 && itemStackTargetSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            windowMovementInfo = WindowMovementInfo.FROM_BAG_TO_EQUIPMENT;
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.BAG_1 && itemStackTargetSlot.getInventoryType() == InventoryType.BAG_1) {
            windowMovementInfo = WindowMovementInfo.FROM_BAG_TO_BAG;
        } else if (sourceItemStackSlot.getInventoryType() == InventoryType.EQUIPMENT && itemStackTargetSlot.getInventoryType() == InventoryType.EQUIPMENT) {
            windowMovementInfo = WindowMovementInfo.FROM_EQUIPMENT_TO_EQUIPMENT;
        }
    }

    /**
     * Enumerator that determines bag movement information.
     * TODO: It may be beneficial to come up with a coordinate system rather
     * TODO: than relying on bag-switch enum. Example below:
     * EquipmentBag: Index bag id 0
     * InventoryBag: Index bag id 1
     * AdditionalBags: Index bag id 2
     * <p>
     * Then use index that represent bag slots. So it could look like this:
     * <p>
     * [InventoryID][BagSlotID] or 0:3 -> (EquipmentBag)(SlotID 3[whatever that would be])
     */
    private enum WindowMovementInfo {
        FROM_BAG_TO_BAG,
        FROM_BAG_TO_EQUIPMENT,
        FROM_EQUIPMENT_TO_BAG,
        FROM_EQUIPMENT_TO_EQUIPMENT;

        private static final String ERROR = "Must implement all cases.";

        private InventoryType getFromWindow() {
            switch (this) {
                case FROM_BAG_TO_BAG:
                    return InventoryType.BAG_1;
                case FROM_BAG_TO_EQUIPMENT:
                    return InventoryType.BAG_1;
                case FROM_EQUIPMENT_TO_BAG:
                    return InventoryType.EQUIPMENT;
                case FROM_EQUIPMENT_TO_EQUIPMENT:
                    return InventoryType.EQUIPMENT;
            }
            throw new RuntimeException(ERROR);
        }

        private InventoryType getToWindow() {
            switch (this) {
                case FROM_BAG_TO_BAG:
                    return InventoryType.BAG_1;
                case FROM_BAG_TO_EQUIPMENT:
                    return InventoryType.EQUIPMENT;
                case FROM_EQUIPMENT_TO_BAG:
                    return InventoryType.BAG_1;
                case FROM_EQUIPMENT_TO_EQUIPMENT:
                    return InventoryType.EQUIPMENT;
            }
            throw new RuntimeException(ERROR);
        }
    }
}
