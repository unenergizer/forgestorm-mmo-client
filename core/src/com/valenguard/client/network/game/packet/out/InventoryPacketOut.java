package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.game.world.item.inventory.InventoryActions;
import com.valenguard.client.network.game.shared.Opcodes;

public class InventoryPacketOut extends AbstractClientOutPacket {

    private final InventoryActions inventoryAction;

    public InventoryPacketOut(InventoryActions inventoryAction) {
        super(Opcodes.INVENTORY_UPDATE);
        this.inventoryAction = inventoryAction;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        InventoryActions.ActionType action = inventoryAction.getActionType();
        write.writeByte(action.getGetActionType());

        if (action == InventoryActions.ActionType.MOVE) {
            write.writeByte(inventoryAction.getFromPosition());
            write.writeByte(inventoryAction.getToPosition());

            // Combining the windows into a single byte.
            byte windowsBytes = (byte) ((inventoryAction.getFromWindow() << 4) | inventoryAction.getToWindow());
            write.writeByte(windowsBytes);
        } else if (action == InventoryActions.ActionType.DROP || action == InventoryActions.ActionType.CONSUME) {

            write.writeByte(inventoryAction.getInteractInventory());
            write.writeByte(inventoryAction.getSlotIndex());
        }
    }
}
