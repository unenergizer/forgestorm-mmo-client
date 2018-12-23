package com.valenguard.client.game.inventory;

import com.valenguard.client.game.assets.GameAtlas;

import lombok.Data;

@Data
public class ItemStack implements Cloneable {

    private int itemId;
    private String name;
    private String description;
    private ItemStackType itemStackType;
    private GameAtlas gameAtlas;
    private boolean isStackable;
    private String textureRegion;
    private int amount;

    public ItemStack(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ItemStack itemStack = new ItemStack(itemId);
        itemStack.setName(name);
        itemStack.setDescription(description);
        itemStack.setItemStackType(itemStackType);
        itemStack.setGameAtlas(gameAtlas);
        itemStack.setStackable(isStackable);
        itemStack.setTextureRegion(textureRegion);
        return super.clone();
    }
}
