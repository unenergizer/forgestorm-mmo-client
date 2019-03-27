package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.inventory.InventoryActions;
import com.valenguard.client.game.world.item.inventory.InventoryMoveData;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

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

        byte fromPosition = -1;
        byte toPosition = -1;
        byte fromWindow = -1;
        byte toWindow = -1;

        if (inventoryAction == InventoryActions.GIVE) {
            itemId = clientHandler.readInt();
            itemAmount = clientHandler.readInt();
        } else if (inventoryAction == InventoryActions.REMOVE) {
            slotIndex = clientHandler.readByte();
        } else if (inventoryAction == InventoryActions.SET_BAG || inventoryAction == InventoryActions.SET_EQUIPMENT) {
            slotIndex = clientHandler.readByte();
            itemId = clientHandler.readInt();
            itemAmount = clientHandler.readInt();
        } else if (inventoryAction == InventoryActions.MOVE) {
            fromPosition = clientHandler.readByte();
            toPosition = clientHandler.readByte();
            byte windowsByte = clientHandler.readByte();
            fromWindow = (byte) (windowsByte >> 4);
            toWindow = (byte) (windowsByte & 0x0F);
        }

        return new InventoryActionsPacket(
                inventoryAction,
                itemId,
                itemAmount,
                slotIndex,
                fromPosition,
                toPosition,
                fromWindow,
                toWindow);
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

        } else if (packetData.inventoryAction == InventoryActions.SET_BAG) {

            ItemStack itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
            ActorUtil.getStageHandler().getBagWindow().setItemStack(packetData.slotIndex, itemStack);

        } else if (packetData.inventoryAction == InventoryActions.SET_EQUIPMENT) {

            ItemStack itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
            ActorUtil.getStageHandler().getEquipmentWindow().setItemStack(packetData.slotIndex, itemStack);

        } else if (packetData.inventoryAction == InventoryActions.MOVE) {

            Valenguard.getInstance().getMoveInventoryEvents().moveItems(new InventoryMoveData(
                    packetData.fromPosition,
                    packetData.toPosition,
                    packetData.fromWindow,
                    packetData.toWindow
            ));
        }
    }

    @AllArgsConstructor
    class InventoryActionsPacket extends PacketData {
        private final byte inventoryAction;
        private final int itemId;
        private final int itemAmount;
        private final byte slotIndex;

        private byte fromPosition;
        private byte toPosition;
        private byte fromWindow;
        private byte toWindow;
    }
}
