package com.valenguard.client.network.packet.out;

import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;

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
        if (action == InventoryActions.MOVE) {
            write.writeByte(inventoryAction.getFromPosition());
            write.writeByte(inventoryAction.getToPosition());
            System.out.println("SENDING FROM WINDOW INDEX: " + inventoryAction.getFromWindow());
            write.writeByte(inventoryAction.getFromWindow());
            System.out.println("SENDING TO WINDOW INDEX: " + inventoryAction.getToWindow());
            write.writeByte(inventoryAction.getToWindow());
        }
    }
}
