package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.widget.VisImage;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.inventory.ItemStack;

import lombok.Getter;

public class InventorySource extends DragAndDrop.Source {

    @Getter
    private InventorySlot inventorySlot;
    private DragAndDrop dragManager;

    InventorySource(InventorySlot inventorySlot, DragAndDrop dragManager) {
        super(inventorySlot);
        this.inventorySlot = inventorySlot;
        this.dragManager = dragManager;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {

        if (target == null) {

            inventorySlot.setItemImage();

        }

    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {

        ItemStack itemStack = inventorySlot.getItemStack();

        if (itemStack == null) {
            return null;
        }

        inventorySlot.setClearImage();

        DragAndDrop.Payload inventoryPayload = new DragAndDrop.Payload();
        inventoryPayload.setObject(itemStack);

        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEM_TEXTURES);

        TextureRegion textureRegion = textureAtlas.findRegion(itemStack.getTextureRegion());
        VisImage image = new VisImage(textureRegion);
        inventoryPayload.setDragActor(image);
        dragManager.setDragActorPosition(image.getWidth() / 2, -image.getHeight() / 2);

//        image = new VisImage(textureRegion);
//        inventoryPayload.setValidDragActor(image);
//
//        image = new VisImage(textureRegion);
//        inventoryPayload.setInvalidDragActor(image);

        return inventoryPayload;
    }
}
