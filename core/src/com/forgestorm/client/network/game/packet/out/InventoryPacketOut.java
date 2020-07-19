package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.item.inventory.InventoryActions;
import com.forgestorm.client.game.world.item.inventory.InventorySyncher;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class InventoryPacketOut extends AbstractClientPacketOut {

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
    protected void createPacket(ForgeStormOutputStream write) {
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
