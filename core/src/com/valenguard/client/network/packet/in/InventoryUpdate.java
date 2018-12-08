package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;
import com.valenguard.client.util.Log;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.INVENTORY_UPDATE)
public class InventoryUpdate implements PacketListener<InventoryUpdate.InventoryActionsPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        byte inventoryAction = clientHandler.readByte();
        int itemId = 0;
        int itemAmount = 0;

        if (inventoryAction == InventoryActions.GIVE) {
            itemId = clientHandler.readInt();
            itemAmount = clientHandler.readInt();
        }

        return new InventoryActionsPacket(inventoryAction, itemId, itemAmount);
    }

    @Override
    public void onEvent(InventoryActionsPacket packetData) {

        if (packetData.inventoryAction == InventoryActions.GIVE) {
            Log.println(getClass(), "Giving the player an item with id: " + packetData.itemId);
            Log.println(getClass(), "Giving the player " + packetData.itemAmount + " of those items");
//            Valenguard.gameScreen.getPlayerInventory().addItemStack(new ItemStack(packetData.itemId, packetData.itemAmount));
        }

    }

    @AllArgsConstructor
    class InventoryActionsPacket extends PacketData {
        byte inventoryAction;
        int itemId;
        int itemAmount;
    }
}
