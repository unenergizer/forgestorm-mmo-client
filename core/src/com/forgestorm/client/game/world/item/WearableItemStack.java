package com.forgestorm.client.game.world.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WearableItemStack extends ItemStack {

    private short textureId;
    private int color;

    public WearableItemStack(int itemId) {
        super(itemId);
    }

    @Override
    public Object clone() {
        WearableItemStack wearableItemStack = (WearableItemStack) super.clone();
        wearableItemStack.setTextureId(textureId);
        wearableItemStack.setColor(color);
        return wearableItemStack;
    }

    @Override
    protected ItemStack generateCloneableInstance() {
        return new WearableItemStack(itemId);
    }

}
