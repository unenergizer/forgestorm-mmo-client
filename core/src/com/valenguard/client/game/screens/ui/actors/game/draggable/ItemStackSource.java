package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.widget.VisImage;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;

public class ItemStackSource extends DragAndDrop.Source {

    private static final int DRAG_IMAGE_SIZE = 24;

    private final StageHandler stageHandler;
    private final DragAndDrop dragManager;
    @Getter
    private final ItemStackSlot itemStackSlot;

    private boolean slotHighlighted = false;
    private ItemStackSlot highlightedEquipmentSlot;

    ItemStackSource(StageHandler stageHandler, DragAndDrop dragManager, ItemStackSlot itemStackSlot) {
        super(itemStackSlot);
        this.stageHandler = stageHandler;
        this.dragManager = dragManager;
        this.itemStackSlot = itemStackSlot;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if (target == null && !itemStackSlot.isTradeSlotLocked()) itemStackSlot.setItemImage();

        // Remove slot highlight
        if (slotHighlighted) {
            stageHandler.getEquipmentWindow().removeSlotHighlight(highlightedEquipmentSlot);
            slotHighlighted = false;
            highlightedEquipmentSlot = null;
        }
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        if (itemStackSlot.isCharacterInspectionSlot()) return null;
        if (itemStackSlot.isTradeSlotLocked()) return null;

        ItemStack itemStack = itemStackSlot.getItemStack();

        if (itemStack == null) return null;

        // Item was picked up, show empty slot image
        itemStackSlot.setEmptyCellImage();

        DragAndDrop.Payload inventoryPayload = new DragAndDrop.Payload();
        inventoryPayload.setObject(itemStack);

        if (stageHandler.getItemDropDownMenu().isVisible()) {
            ActorUtil.fadeOutWindow(stageHandler.getItemDropDownMenu());
        }

        // The image to display when the item is picked up and is being moved to valid locations
        VisImage image = new ImageBuilder(GameAtlas.ITEMS, itemStack.getTextureRegion(), DRAG_IMAGE_SIZE).buildVisImage();
        inventoryPayload.setDragActor(image);

        // The image to display when the item is being placed over an invalid location
        image = new ImageBuilder(GameAtlas.ITEMS, itemStack.getTextureRegion(), DRAG_IMAGE_SIZE).buildVisImage();
        image.setColor(Color.RED);
        inventoryPayload.setInvalidDragActor(image);

        // Highlight acceptable slot, if the item is wearable
        EquipmentWindow equipmentWindow = stageHandler.getEquipmentWindow();
        if (itemStack.getItemStackType().isEquipable() && equipmentWindow.isVisible()) {
            highlightedEquipmentSlot = equipmentWindow.addSlotHighlight(itemStack);
            slotHighlighted = true;
        }

        // Sets where the image will be shown relative to the mouse
        dragManager.setDragActorPosition(image.getWidth() / 2, -image.getHeight() / 2);

        // Play Sound FX
        Valenguard.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), itemStackSlot.getItemStack());

        return inventoryPayload;
    }
}
