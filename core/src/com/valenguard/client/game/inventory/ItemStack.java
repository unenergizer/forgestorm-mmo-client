package com.valenguard.client.game.inventory;

import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.rpg.Attributes;

import lombok.Data;

@SuppressWarnings("WeakerAccess")
@Data
public class ItemStack implements Cloneable {

    protected int itemId;
    private String name;
    private String description;
    private ItemStackType itemStackType;
    private GameAtlas gameAtlas;
    private boolean isStackable;
    private String textureRegion;
    private int amount;

    private Attributes attributes;

    public ItemStack(int itemId) {
        this.itemId = itemId;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        ItemStack itemStack = generateCloneableInstance();
        itemStack.setName(name);
        itemStack.setDescription(description);
        itemStack.setItemStackType(itemStackType);
        itemStack.setGameAtlas(gameAtlas);
        itemStack.setStackable(isStackable);
        itemStack.setTextureRegion(textureRegion);
        itemStack.setAttributes(attributes);
        return itemStack;
    }

    @SuppressWarnings("WeakerAccess")
    protected ItemStack generateCloneableInstance() {
        return new ItemStack(itemId);
    }
}
