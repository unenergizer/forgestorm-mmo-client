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
            case SET_HOT_BAR:
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
                        packetData.toWindow,
                        false,
                        0

                ));
                break;
            case CONSUME:
                Valenguard.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                println(getClass(), "The client consumed an item!", false, PRINT_DEBUG);
                // TODO: later on we would change the visible display of the itemstack to represent
                // TODO: the amount of consumption left on the item
                break;
            case USE:
                // TODO
                break;
            case GIVE:
                Valenguard.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getBagWindow().getItemSlotContainer().addItemStack(itemStack);
                println(getClass(), packetData.actionType + ": Setting the item: " + itemStack + " at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
                break;
            case REMOVE:
                Valenguard.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                ActorUtil.getStageHandler().getBagWindow().getItemSlotContainer().removeItemStack(packetData.slotIndex);
                println(getClass(), packetData.actionType + ": Removing the item at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
                break;
            case SET_BAG:
                Valenguard.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getBagWindow().getItemSlotContainer().setItemStack(packetData.slotIndex, itemStack);
                println(getClass(), packetData.actionType + ": Setting the item: " + itemStack + " at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
                break;
            case SET_BANK:
                Valenguard.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getBankWindow().getItemSlotContainer().setItemStack(packetData.slotIndex, itemStack);
                println(getClass(), packetData.actionType + ": Setting the item: " + itemStack + " at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
                break;
            case SET_EQUIPMENT:
                Valenguard.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getEquipmentWindow().getItemSlotContainer().setItemStack(packetData.slotIndex, itemStack);
                println(getClass(), packetData.actionType + ": Setting the item: " + itemStack + " at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
                break;
            case SET_HOT_BAR:
                Valenguard.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                itemStack = Valenguard.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ActorUtil.getStageHandler().getHotBar().getItemSlotContainer().setItemStack(packetData.slotIndex, itemStack);
                println(getClass(), packetData.actionType + ": Setting the item: " + itemStack + " at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
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
