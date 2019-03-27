package com.valenguard.client.game.world.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WearableItemStack extends ItemStack {

    private short textureId;

    public WearableItemStack(int itemId) {
        super(itemId);
    }

    @Override
    public Object clone() {
        WearableItemStack wearableItemStack = (WearableItemStack) super.clone();
        wearableItemStack.setTextureId(textureId);
        return wearableItemStack;
    }

    @Override
    protected ItemStack generateCloneableInstance() {
        return new WearableItemStack(itemId);
    }

}
