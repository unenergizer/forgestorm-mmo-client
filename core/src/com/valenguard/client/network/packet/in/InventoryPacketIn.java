package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.InventoryActions;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INVENTORY_UPDATE)
public class InventoryPacketIn implements PacketListener<InventoryPacketIn.InventoryActionsPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        byte inventoryAction = clientHandler.readByte();
        int itemId = 0;
        int itemAmount = 0;
        byte slotIndex = 0;

        if (inventoryAction == InventoryActions.GIVE) {
            itemId = clientHandler.readInt();
            itemAmount = clientHandler.readInt();
        } else if (inventoryAction == InventoryActions.REMOVE) {
            slotIndex = clientHandler.readByte();
        }

        return new InventoryActionsPacket(inventoryAction, itemId, itemAmount, slotIndex);
    }

    @Override
    public void onEvent(InventoryActionsPacket packetData) {

        if (packetData.inventoryAction == InventoryActions.GIVE) {
            println(getClass(), "Giving ItemStack id: " + packetData.itemId + ", Amount: " + packetData.itemAmount, false, PRINT_DEBUG);

            // Generate an ItemStack and place it in the players bag.
            ItemStack itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
            ActorUtil.getStageHandler().getBagWindow().addItemStack(itemStack);
        } else if (packetData.inventoryAction == InventoryActions.REMOVE) {

            ActorUtil.getStageHandler().getBagWindow().removeItemStack(packetData.slotIndex);

        }
    }

    @AllArgsConstructor
    class InventoryActionsPacket extends PacketData {
        private final byte inventoryAction;
        private final int itemId;
        private final int itemAmount;
        private final byte slotIndex;
    }
}
