package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.widget.VisImage;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.ImageBuilder;

import lombok.Getter;

public class ItemStackSource extends DragAndDrop.Source {

    private static final int DRAG_IMAGE_SIZE = 24;

    @Getter
    private ItemStackSlot itemStackSlot;
    private DragAndDrop dragManager;

    ItemStackSource(ItemStackSlot itemStackSlot, DragAndDrop dragManager) {
        super(itemStackSlot);
        this.itemStackSlot = itemStackSlot;
        this.dragManager = dragManager;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if (target == null) itemStackSlot.setItemImage();
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        ItemStack itemStack = itemStackSlot.getItemStack();

        if (itemStack == null) return null;

        itemStackSlot.setEmptyCellImage();

        DragAndDrop.Payload inventoryPayload = new DragAndDrop.Payload();
        inventoryPayload.setObject(itemStack);

        VisImage image = new ImageBuilder(GameAtlas.ITEM_TEXTURES, itemStack.getTextureRegion(), DRAG_IMAGE_SIZE).buildVisImage();
        inventoryPayload.setDragActor(image);

        image = new ImageBuilder(GameAtlas.ITEM_TEXTURES, itemStack.getTextureRegion(), DRAG_IMAGE_SIZE).buildVisImage();
        image.setColor(Color.RED);
        inventoryPayload.setInvalidDragActor(image);

        dragManager.setDragActorPosition(image.getWidth() / 2, -image.getHeight() / 2);
        return inventoryPayload;
    }
}
