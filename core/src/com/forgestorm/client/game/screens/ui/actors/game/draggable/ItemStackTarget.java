package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.game.world.item.ItemStackType;
import com.forgestorm.client.game.world.item.inventory.InventoryType;

import static com.forgestorm.client.util.Log.println;

public class ItemStackTarget extends DragAndDrop.Target {

    /**
     * The slot that at {@link ItemStack} is being dragged too.
     */
    private final ItemStackSlot itemStackTargetSlot;

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

        // Prevent the movement of BOOK_SKILL items
        if (sourceItemStackSlot.getItemStack().getItemStackType() == ItemStackType.BOOK_SKILL) {
            if (itemStackTargetSlot.getInventoryType() != InventoryType.HOT_BAR) return false;
        }

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

        // Play Sound FX
        ClientMain.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), sourceItemStack);

        // The client is simply picking up and placing down the item in the exact same position.
        if (sourceItemStackSlot.getSlotIndex() == itemStackTargetSlot.getSlotIndex() &&
                sourceItemStackSlot.getInventoryType() == itemStackTargetSlot.getInventoryType()) {
            itemStackTargetSlot.setItemStack(sourceItemStack);
            sourceItemStackSlot.setItemStack(targetItemStack);
            println(getClass(), "Picking up and placing back down", false, PRINT_DEBUG);
            return;
        }

        new InventoryMoveActions().moveItems(sourceItemStackSlot, itemStackTargetSlot, sourceItemStack, targetItemStack);
    }
}
