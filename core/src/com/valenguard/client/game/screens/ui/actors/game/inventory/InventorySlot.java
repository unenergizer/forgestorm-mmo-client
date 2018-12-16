package com.valenguard.client.game.screens.ui.actors.game.inventory;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.actors.Buildable;

import lombok.Getter;

public class InventorySlot extends VisTable implements Buildable {

    @Getter
    private ItemStack itemStack;

    private VisImage imageItem;

    private VisImage clearImage;

    InventorySlot(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public Actor build() {
        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEM_TEXTURES);
        TextureRegion textureRegionClear = textureAtlas.findRegion("clear");
        clearImage = new VisImage(textureRegionClear);
        if (itemStack == null) {
            add(clearImage);
        } else {
            TextureRegion textureRegionItem = textureAtlas.findRegion(itemStack.getTextureRegion());
            imageItem = new VisImage(textureRegionItem);
            add(imageItem);
        }
        return this;
    }

    void deleteStack() {
        itemStack = null;
    }

    void setClearImage() {
        if (imageItem != null) imageItem.remove();
        add(clearImage);
    }

    void setItemImage() {
        clearImage.remove();
        add(imageItem);
    }

    void setStack(ItemStack itemStack) {
        if (imageItem != null) imageItem.remove();
        this.itemStack = itemStack;
        clearImage.remove();
        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEM_TEXTURES);
        TextureRegion textureRegion = textureAtlas.findRegion(itemStack.getTextureRegion());
        imageItem = new VisImage(textureRegion);
        add(imageItem);
    }
}
