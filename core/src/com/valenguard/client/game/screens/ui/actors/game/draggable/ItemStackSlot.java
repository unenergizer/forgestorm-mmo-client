package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.inventory.InventoryType;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.Buildable;

import lombok.Getter;

public class ItemStackSlot extends VisTable implements Buildable {

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    private final ItemStackSlot itemStackSlot;
    @Getter
    private InventoryType inventoryType;
    @Getter
    private ItemStack itemStack;
    @Getter
    private ItemStackType itemStackType;
    private VisImage itemStackImage;
    private VisImage emptyCellImage;
    private ItemStackToolTip itemStackToolTip = new ItemStackToolTip();

    ItemStackSlot(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
        this.itemStackSlot = this;
    }

    ItemStackSlot(InventoryType inventoryType, ItemStackType itemStackType) {
        this.inventoryType = inventoryType;
        this.itemStackType = itemStackType;
        this.itemStackSlot = this;
    }

    @Override
    public Actor build() {
        Valenguard.getInstance().getStageHandler().getStage().addActor(itemStackToolTip.build());
        if (itemStack == null) {
            setEmptyCellImage();
        } else {
            setItemStack(itemStack);
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
        itemStackToolTip.updateToolTipText(itemStack);
    }

    void setItemStack(ItemStack itemStack) {
        if (itemStackImage != null) itemStackImage.remove();
        this.itemStack = itemStack;
        emptyCellImage.remove();
        itemStackImage = new ImageBuilder(GameAtlas.ITEMS, 32).setRegionName(itemStack.getTextureRegion()).buildVisImage();
        add(itemStackImage);
        itemStackToolTip.updateToolTipText(itemStack);
        addToolTipListener();
    }

    public static Vector2 getStageLocation(Actor actor) {
        return actor.localToStageCoordinates(new Vector2(0, 0));
    }

    private void addToolTipListener() {
        itemStackImage.addListener(new InputListener() {

            /** Called any time the mouse cursor or a finger touch is moved over an actor. On the desktop, this event occurs even when no
             * mouse buttons are pressed (pointer will be -1).
             * @param fromActor May be null.
             * @see InputEvent */
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Vector2 vec = getStageLocation(itemStackSlot);
                itemStackToolTip.toFront();
                itemStackToolTip.fadeIn().setVisible(true);
                itemStackToolTip.setPosition(vec.x - itemStackToolTip.getWidth(), vec.y + itemStackSlot.getHeight());
            }

            /** Called any time the mouse cursor or a finger touch is moved out of an actor. On the desktop, this event occurs even when no
             * mouse buttons are pressed (pointer will be -1).
             * @param toActor May be null.
             * @see InputEvent */
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                itemStackToolTip.fadeOut();
            }
        });
    }
}
