package com.valenguard.client.game.inventory;

import lombok.Getter;

@Getter
public class ItemStack {
    private boolean isStackable;
    private int itemId;
    private int amount;

    public ItemStack(int itemId, int amount) {
        this.itemId = itemId;
        // TODO get isStackable based on the item type / inventory type such as bank/player inventory ect..
        this.amount = amount;
    }
}
