package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.item.inventory.InventoryActions;
import com.forgestorm.client.game.world.item.inventory.InventorySyncher;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class InventoryPacketOut extends AbstractClientPacketOut {

    private static final boolean PRINT_DEBUG = false;
    private final InventoryActions inventoryAction;

    public InventoryPacketOut(InventoryActions inventoryAction) {
        super(Opcodes.INVENTORY_UPDATE);
        InventorySyncher.addPreviousAction(inventoryAction);
        this.inventoryAction = inventoryAction;
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        InventoryActions.ActionType action = inventoryAction.getActionType();
        write.writeByte(action.getGetActionType());

        println(getClass(), "", false, PRINT_DEBUG);
        println(getClass(), "Creating packet. ActionType: " + action.toString(), false, PRINT_DEBUG);

        if (action == InventoryActions.ActionType.MOVE) {

            write.writeByte(inventoryAction.getFromPosition());
            write.writeByte(inventoryAction.getToPosition());

            // Combining the windows into a single byte.
            byte windowsBytes = (byte) ((inventoryAction.getFromWindow() << 4) | inventoryAction.getToWindow());

            write.writeByte(windowsBytes);

        } else if (action == InventoryActions.ActionType.DROP
                || action == InventoryActions.ActionType.CONSUME
                || action == InventoryActions.ActionType.REMOVE) {

            write.writeByte(inventoryAction.getInteractInventory());
            write.writeByte(inventoryAction.getSlotIndex());

            println(getClass(), "InteractiveInventory: " + inventoryAction.getInteractInventory(), false, PRINT_DEBUG);
            println(getClass(), "SlotIndex: " + inventoryAction.getSlotIndex(), false, PRINT_DEBUG);
        }
    }
}
