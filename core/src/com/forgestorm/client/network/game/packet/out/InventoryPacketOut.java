package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.item.inventory.InventoryActions;
import com.forgestorm.client.game.world.item.inventory.InventorySyncher;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class InventoryPacketOut extends AbstractPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final InventoryActions.ActionType actionType;
    private final byte fromPosition;
    private final byte toPosition;
    private final byte fromWindow;
    private final byte toWindow;
    private final byte interactInventory;
    private final byte slotIndex;

    public InventoryPacketOut(InventoryActions inventoryAction) {
        super(Opcodes.INVENTORY_UPDATE);
        InventorySyncher.addPreviousAction(inventoryAction);

        this.actionType = inventoryAction.getActionType();
        this.fromPosition = inventoryAction.getFromPosition();
        this.toPosition = inventoryAction.getToPosition();
        this.fromWindow = inventoryAction.getFromWindow();
        this.toWindow = inventoryAction.getToWindow();
        this.interactInventory = inventoryAction.getInteractInventory();
        this.slotIndex = inventoryAction.getSlotIndex();
    }

    @Override
    public void createPacket(GameOutputStream write) {
        InventoryActions.ActionType action = actionType;
        write.writeByte(action.getGetActionType());

        println(getClass(), "", false, PRINT_DEBUG);
        println(getClass(), "Creating packet. ActionType: " + action.toString(), false, PRINT_DEBUG);

        if (action == InventoryActions.ActionType.MOVE) {

            write.writeByte(fromPosition);
            write.writeByte(toPosition);

            // Combining the windows into a single byte.
            byte windowsBytes = (byte) ((fromWindow << 4) | toWindow);

            write.writeByte(windowsBytes);

        } else if (action == InventoryActions.ActionType.DROP
                || action == InventoryActions.ActionType.CONSUME
                || action == InventoryActions.ActionType.REMOVE) {

            write.writeByte(interactInventory);
            write.writeByte(slotIndex);

            println(getClass(), "InteractiveInventory: " + interactInventory, false, PRINT_DEBUG);
            println(getClass(), "SlotIndex: " + slotIndex, false, PRINT_DEBUG);
        }
    }
}
