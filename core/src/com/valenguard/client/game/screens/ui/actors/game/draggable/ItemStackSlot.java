package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.Buildable;

import lombok.Getter;

public class ItemStackSlot extends VisTable implements Buildable {

    @Getter
    private ItemStack itemStack;
    @Getter
    private ItemStackType itemStackType;
    private VisImage imageItem;
    private VisImage clearImage;

    ItemStackSlot() {
    }

    ItemStackSlot(ItemStackType itemStackType) {
        this.itemStackType = itemStackType;
    }

    @Override
    public Actor build() {
        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEM_TEXTURES, 32);
        clearImage = imageBuilder.setRegionName("clear").buildVisImage();
        clearImage.setColor(Color.GREEN);
        if (itemStack == null) {
            add(clearImage);
        } else {
            imageItem = imageBuilder.setRegionName(itemStack.getTextureRegion()).buildVisImage();
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
        imageItem = new ImageBuilder(GameAtlas.ITEM_TEXTURES, 32).setRegionName(itemStack.getTextureRegion()).buildVisImage();
        add(imageItem);
    }
}
