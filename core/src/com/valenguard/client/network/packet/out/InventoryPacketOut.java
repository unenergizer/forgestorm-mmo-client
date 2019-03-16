package com.valenguard.client.network.packet.out;

import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.network.shared.Opcodes;

public class InventoryPacketOut extends AbstractClientOutPacket {

    private final InventoryActions inventoryAction;

    public InventoryPacketOut(InventoryActions inventoryAction) {
        super(Opcodes.INVENTORY_UPDATE);
        this.inventoryAction = inventoryAction;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        byte action = inventoryAction.getInventoryActionType();
        write.writeByte(action);
        if (action == InventoryActions.MOVE) {

            write.writeByte(inventoryAction.getFromPosition());
            write.writeByte(inventoryAction.getToPosition());

            // Combining the windows into a single byte.
            byte windowsBytes = (byte) ((inventoryAction.getFromWindow() << 4) | inventoryAction.getToWindow());
            write.writeByte(windowsBytes);
        }
    }
}
