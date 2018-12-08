package com.valenguard.client.network.packet.out;

import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class InventoryChangePacket extends ClientOutPacket {

    private InventoryActions inventoryAction;

    public InventoryChangePacket(InventoryActions inventoryAction) {
        super(Opcodes.INVENTORY_UPDATE);
        this.inventoryAction = inventoryAction;
    }

    @Override
    protected void createPacket(DataOutputStream write) throws IOException {
        byte action = inventoryAction.getInventoryActionType();
        write.writeByte(action);
        write.writeByte(inventoryAction.getClickedPosition());
        if (action == InventoryActions.MOVE) {
            write.writeByte(inventoryAction.getToPosition());
        }
    }
}
