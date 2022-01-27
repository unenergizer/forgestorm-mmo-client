package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemSlotContainer;
import com.forgestorm.client.game.world.item.inventory.InventoryActions;
import com.forgestorm.client.game.world.item.inventory.InventorySyncher;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.inventory.InventoryUtil;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INVENTORY_UPDATE)
public class InventoryPacketIn implements PacketListener<InventoryPacketIn.InventoryActionsPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        byte inventoryAction = clientHandler.readByte();
        int itemId = 0;
        int itemAmount = 0;
        byte slotIndex = 0;
        byte interactiveInventory = 0;

        byte fromPosition = 0;
        byte toPosition = 0;
        byte fromWindow = 0;
        byte toWindow = 0;

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
            case USE:
                // TODO
                break;
            case REMOVE:
                interactiveInventory = clientHandler.readByte();
                slotIndex = clientHandler.readByte();
                break;
            case SET:
                interactiveInventory = clientHandler.readByte();
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
                interactiveInventory,
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
                /*ClientMain.getInstance().getMoveInventoryEvents().moveItems(new InventoryMoveData(
                        packetData.fromPosition,
                        packetData.toPosition,
                        packetData.fromWindow,
                        packetData.toWindow,
                        false,
                        0

                ));*/
                break;
            case CONSUME:
                // ClientMain.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                println(getClass(), "The client consumed an item!", false, PRINT_DEBUG);
                // TODO: later on we would change the visible display of the itemstack to represent
                // TODO: the amount of consumption left on the item
                break;
            case USE:
                // TODO
                break;
            case REMOVE:
                //ClientMain.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                ItemSlotContainer removeContainer = InventoryUtil.getItemSlotContainer(packetData.interactiveInventory);
                removeContainer.removeItemStack(packetData.slotIndex);
                println(getClass(), packetData.actionType + ": Removing the item at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
                break;
            case SET:
                //ClientMain.getInstance().getMoveInventoryEvents().receivedNonMoveRequest();
                itemStack = ClientMain.getInstance().getItemStackManager().makeItemStack(packetData.itemId, packetData.itemAmount);
                ItemSlotContainer setContainer = InventoryUtil.getItemSlotContainer(packetData.interactiveInventory);
                setContainer.setItemStack(packetData.slotIndex, itemStack);
                println(getClass(), packetData.actionType + ": Setting the item: " + itemStack + " at slot index: " + packetData.slotIndex, false, PRINT_DEBUG);
                break;
        }

        //byte fromWindow, byte toWindow, byte fromPosition, byte toPosition,
        //                            byte interactInventory, byte slotIndex

        InventoryActions action = new InventoryActions(packetData.actionType, packetData.fromWindow, packetData.toWindow,
                packetData.fromPosition, packetData.toPosition, packetData.interactiveInventory, packetData.slotIndex);

        InventorySyncher.serverActionResponse(action);
    }

    @AllArgsConstructor
    class InventoryActionsPacket extends PacketData {
        private final InventoryActions.ActionType actionType;
        private final int itemId;
        private final int itemAmount;
        private final byte slotIndex;

        private final byte interactiveInventory;
        private final byte fromPosition;
        private final byte toPosition;
        private final byte fromWindow;
        private final byte toWindow;
    }
}