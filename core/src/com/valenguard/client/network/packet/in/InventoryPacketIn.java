package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INVENTORY_UPDATE)
public class InventoryPacketIn implements PacketListener<InventoryPacketIn.InventoryActionsPacket> {

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
            println(getClass(), "Giving the player an item with id: " + packetData.itemId);
            println(getClass(), "Giving the player " + packetData.itemAmount + " of those items");

            ItemStack itemStack = Valenguard.getInstance().getItemManager().makeItemStack(packetData.itemId, packetData.itemAmount);
            Valenguard.getInstance().getStageHandler().getBagWindow().addItemStack(itemStack);

        }

    }

    @AllArgsConstructor
    class InventoryActionsPacket extends PacketData {
        byte inventoryAction;
        int itemId;
        int itemAmount;
    }
}
