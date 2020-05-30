package com.valenguard.client.game.world.item.inventory;

import java.util.LinkedList;
import java.util.Queue;

import static com.valenguard.client.util.Log.println;

public class InventorySyncher {

    private static final boolean PRINT_DEBUG = false;

    private static final Queue<InventoryActions> previousInventoryActions = new LinkedList<InventoryActions>();

    public static void addPreviousAction(InventoryActions inventoryAction) {
        previousInventoryActions.add(inventoryAction);
    }

    public static void serverActionResponse(InventoryActions responseAction) {

        // The server is telling the inventory what to do
        // but the client has already processed all of it's
        // actions
        if (previousInventoryActions.isEmpty()) {
            return;
        }

        // If the response matches an already processed action
        // then the client and server are in sync. Otherwise they
        // are out of sync

        InventoryActions action = previousInventoryActions.remove();
        if (!action.equals(responseAction)) {
            println(InventorySyncher.class, "Last action: " + action, true);
            println(InventorySyncher.class, "Action that caused lack of syncing: " + responseAction, true);
            println(InventorySyncher.class, "The inventories are out of sync and need resyncing", true);
            // TODO: write complicated code for syncing the inventory
        } else {
            println(InventorySyncher.class, "The inventories are in sync", false, PRINT_DEBUG);
        }
    }
}
