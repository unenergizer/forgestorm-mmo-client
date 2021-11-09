package com.forgestorm.shared.game.world.item;

import com.forgestorm.shared.game.rpg.Attributes;
import com.forgestorm.shared.io.type.GameAtlas;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ItemStack implements Cloneable, Serializable {

    private int itemId;
    private transient String name;
    private transient String description;
    private transient ItemStackType itemStackType;
    private transient GameAtlas gameAtlas;
    private transient String textureRegion;
    private transient int stackable;
    private int amount;
    private transient boolean isConsumable;

    // Any regular item can have a skill ID attached to it.
    // A skill could be a player skill or a special item with a cool down.
    private transient Integer skillID;

    private transient Attributes attributes;

    public ItemStack(int itemId) {
        this.itemId = itemId;
    }

    public ItemStack(ItemStack itemStack) {
        this.itemId = itemStack.getItemId();
        this.name = itemStack.getName();
        this.description = itemStack.getDescription();
        this.itemStackType = itemStack.getItemStackType();
        this.stackable = itemStack.getStackable();
        this.attributes = itemStack.getAttributes();
        this.amount = itemStack.getAmount();
        this.isConsumable = itemStack.isConsumable();
        this.skillID = itemStack.getSkillID();
    }

    @Override
    public Object clone() {
        return clone(false);
    }

    public Object clone(boolean keepAmount) {
        ItemStack itemStack = generateCloneableInstance();
        itemStack.setName(name);
        itemStack.setDescription(description);
        itemStack.setItemStackType(itemStackType);
        itemStack.setGameAtlas(gameAtlas);
        itemStack.setTextureRegion(textureRegion);
        itemStack.setStackable(stackable);
        itemStack.setAttributes(attributes);
        if (keepAmount) itemStack.setAmount(amount);
        itemStack.setConsumable(isConsumable);
        itemStack.setSkillID(skillID);
        return itemStack;
    }

    protected ItemStack generateCloneableInstance() {
        return new ItemStack(itemId);
    }
}
