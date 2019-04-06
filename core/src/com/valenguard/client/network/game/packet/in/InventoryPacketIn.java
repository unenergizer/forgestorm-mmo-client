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

@Opcode(getOpcode = Opcodes.INVENTORY_UPDATE)
public class InventoryPacketIn implements PacketListener<InventoryPacketIn.InventoryActionsPacket> {

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

        InventoryActions.ActionType actionType = InventoryActions.ActionType.getActionType(inventoryAction);

        switch (actionType) {
            case MOVE:
                fromPosition = clientHandler.readByte();
                toPosition = clientHandler.readByte();
                byte windowsByte = clientHandler.readByte();
                fromWindow = (byte) (windowsByte >> 4);
                toWindow = (byte) (windowsByte & 0x0F);
                break;
            case DROP:
                // TODO
                break;
            case USE:
                // TODO
                break;
            case GIVE:
                itemId = clientHandler.readInt();
                itemAmount = clientHandler.readInt();
                break;
            case REMOVE:
                slotIndex = clientHandler.readByte();
                break;
            case SET_BAG:
            case SET_BANK:
            case SET_EQUIPMENT:
                slotIndex = clientHandler.readByte();
                itemId = clientHandler.readInt();
                itemAmount = clientHandler.readInt();
                break;
        }

        return new InventoryActionsPacket(
                actionType,
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

        ItemStack itemStack;

        switch (packetData.actionType) {
            case MOVE:
                Valenguard.getInstance().getMoveInventoryEvents().moveItems(new InventoryMoveData(
                        packetData.fromPosition,
                        packetData.toPosition,
                        packetData.fromWindow,
                        packetData.toWindow
                ));
                break;
            case DROP:
                // TODO
                break;
            case USE:
                // TODO
                break;
            case GIVE:
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getBagWindow().addItemStack(itemStack);
                break;
            case REMOVE:
                ActorUtil.getStageHandler().getBagWindow().removeItemStack(packetData.slotIndex);
                break;
            case SET_BAG:
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getBagWindow().setItemStack(packetData.slotIndex, itemStack);
                break;
            case SET_BANK:
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getBankWindow().setItemStack(packetData.slotIndex, itemStack);
                break;
            case SET_EQUIPMENT:
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getEquipmentWindow().setItemStack(packetData.slotIndex, itemStack);
                break;
        }
    }

    @AllArgsConstructor
    class InventoryActionsPacket extends PacketData {
        private final InventoryActions.ActionType actionType;
        private final int itemId;
        private final int itemAmount;
        private final byte slotIndex;

        private byte fromPosition;
        private byte toPosition;
        private byte fromWindow;
        private byte toWindow;
    }
}
