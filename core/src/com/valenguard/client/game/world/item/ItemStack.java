package com.valenguard.client.game.world.item;

import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Data;

@SuppressWarnings("WeakerAccess")
@Data
public class ItemStack implements Cloneable {

    protected int itemId;
    private String name;
    private String description;
    private ItemStackType itemStackType;
    private GameAtlas gameAtlas;
    private int stackable;
    private String textureRegion;
    private int amount;
    private boolean isConsumable;

    // Any regular item can have a skill ID attached to it.
    // A skill could be a player skill or a special item with a cool down.
    private Integer skillID;

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
        itemStack.setStackable(stackable);
        itemStack.setTextureRegion(textureRegion);
        itemStack.setAttributes(attributes);
        itemStack.setConsumable(isConsumable);
        itemStack.setSkillID(skillID);
        return itemStack;
    }

    @SuppressWarnings("WeakerAccess")
    protected ItemStack generateCloneableInstance() {
        return new ItemStack(itemId);
    }
}
