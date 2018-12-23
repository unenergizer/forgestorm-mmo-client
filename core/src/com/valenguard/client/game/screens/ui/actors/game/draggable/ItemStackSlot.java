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

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);
    @Getter
    private ItemStack itemStack;
    @Getter
    private ItemStackType itemStackType;
    private VisImage itemStackImage;
    private VisImage emptyCellImage;

    ItemStackSlot() {
    }

    ItemStackSlot(ItemStackType itemStackType) {
        this.itemStackType = itemStackType;
    }

    @Override
    public Actor build() {
        if (itemStack == null) {
            initEmptyCellImage();
            add(emptyCellImage);
        } else {
            itemStackImage = imageBuilder.setRegionName(itemStack.getTextureRegion()).buildVisImage();
            add(itemStackImage);
        }
        return this;
    }

    private void initEmptyCellImage() {
        if (itemStackType != null) {
            switch (itemStackType) {
                case HELM:
                    emptyCellImage = imageBuilder.setRegionName("helmet_08").buildVisImage();
                    break;
                case CHEST:
                    emptyCellImage = imageBuilder.setRegionName("armor_001").buildVisImage();
                    break;
                case BOOTS:
                    emptyCellImage = imageBuilder.setRegionName("boot_02").buildVisImage();
                    break;
                case CAPE:
                    emptyCellImage = imageBuilder.setRegionName("armor_035").buildVisImage();
                    break;
                case GLOVES:
                    emptyCellImage = imageBuilder.setRegionName("glove_01").buildVisImage();
                    break;
                case BELT:
                    emptyCellImage = imageBuilder.setRegionName("accessory_01").buildVisImage();
                    break;
                case RINGS:
                    emptyCellImage = imageBuilder.setRegionName("ring_001").buildVisImage();
                    break;
                case NECKLACE:
                    emptyCellImage = imageBuilder.setRegionName("accessory_04").buildVisImage();
                    break;
                case SWORD:
                case BOW:
                    emptyCellImage = imageBuilder.setRegionName("weapon_sword_01").buildVisImage();
                    break;
                case SHIELD:
                    emptyCellImage = imageBuilder.setRegionName("shield_01").buildVisImage();
                    break;
                case ARROW:
                    emptyCellImage = imageBuilder.setRegionName("weapon_arrow_01").buildVisImage();
                    break;
            }
            emptyCellImage.setColor(new Color(1, 1, 1, .1f));
        } else {
            emptyCellImage = imageBuilder.setRegionName("clear").buildVisImage();
        }
    }

    void deleteStack() {
        itemStack = null;
    }

    void setEmptyCellImage() {
        if (itemStackImage != null) itemStackImage.remove();
        if (emptyCellImage == null) initEmptyCellImage();
        add(emptyCellImage);
    }

    void setItemImage() {
        emptyCellImage.remove();
        add(itemStackImage);
    }

    void setStack(ItemStack itemStack) {
        if (itemStackImage != null) itemStackImage.remove();
        this.itemStack = itemStack;
        emptyCellImage.remove();
        itemStackImage = new ImageBuilder(GameAtlas.ITEMS, 32).setRegionName(itemStack.getTextureRegion()).buildVisImage();
        add(itemStackImage);
    }
}
