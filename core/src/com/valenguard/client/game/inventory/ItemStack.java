package com.valenguard.client.game.inventory;

import com.valenguard.client.Valenguard;

import lombok.Getter;

@Getter
public class ItemStack {
    private ItemStackType itemStackType;
    private boolean isStackable;
    private String textureRegion;
    private int itemId;
    private int amount;

    public ItemStack(int itemId, int amount) {
        this.itemId = itemId;
        // TODO get isStackable based on the item type / inventory type such as bank/player inventory ect..
        this.amount = amount;
        this.textureRegion = Valenguard.getInstance().getItemManager().getItemName(itemId);
        this.itemStackType = ItemStackType.SWORD; // TODO: Get type from ConfigFile
        System.out.println("TEXTURE REGION = " + textureRegion);
    }
}
