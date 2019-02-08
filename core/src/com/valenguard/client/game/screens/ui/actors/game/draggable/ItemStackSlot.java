package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
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

import static com.valenguard.client.util.Log.println;

public class ItemStackSlot extends VisTable implements Buildable {

    /**
     * Used to places images in the UserInterface
     */
    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    /**
     * Represents this {@link ItemStackSlot}
     */
    private final ItemStackSlot itemStackSlot;

    /**
     * The slot index of this {@link ItemStackSlot}. This ID is used as locational data
     * for the server and the client.
     */
    @Getter
    private byte inventoryIndex;

    /**
     * The type of inventory that this {@link ItemStackSlot} represents
     */
    @Getter
    private InventoryType inventoryType;

    /**
     * The {@link ItemStack} that is contained within this {@link ItemStackSlot}
     */
    @Getter
    private ItemStack itemStack;

    /**
     * The types of {@link ItemStackType}s allowed in this {@link ItemStackSlot}
     * If null, we assume that this {@link ItemStackSlot} will accept any type of item.
     */
    @Getter
    private ItemStackType[] acceptedItemStackTypes;

    /**
     * The image that represents this {@link ItemStackSlot}
     */
    private VisImage itemStackImage;

    /**
     * The image that represents this as an empty {@link ItemStackSlot}
     */
    private VisImage emptyCellImage;

    /**
     * A user interface element that displays information about a given {@link ItemStack}
     */
    private final ItemStackToolTip itemStackToolTip = new ItemStackToolTip();

    /**
     * Camera coordinates in relation to the game screen.
     */
    private final Vector2 localCords = new Vector2();
    private Vector2 stageLocation = new Vector2();

    ItemStackSlot(InventoryType inventoryType, byte inventoryIndex) {
        this.inventoryType = inventoryType;
        this.inventoryIndex = inventoryIndex;
        this.itemStackSlot = this;
    }

    ItemStackSlot(byte inventoryIndex, ItemStackType[] acceptedItemStackTypes) {
        this.inventoryType = InventoryType.EQUIPMENT;
        this.inventoryIndex = inventoryIndex;
        this.acceptedItemStackTypes = acceptedItemStackTypes;
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

    /**
     * Check to see if the {@link ItemStack} is able to be placed in this slot.
     *
     * @param itemStack The {@link ItemStack} to test.
     * @return True if accepted, false otherwise.
     */
    boolean isAcceptedItemStackType(ItemStack itemStack) {
        if (acceptedItemStackTypes == null) return true; // Accepts any type of ItemStackType

        for (ItemStackType itemStackType : acceptedItemStackTypes) {
            if (itemStackType == itemStack.getItemStackType()) return true;
        }

        return false;
    }

    /**
     * Creates an image that represents an empty {@link ItemStackSlot}.
     */
    private void initEmptyCellImage() {
        if (acceptedItemStackTypes == null) {
            // Used on bag slots
            emptyCellImage = imageBuilder.setRegionName("clear").buildVisImage();
        } else {
            // Used on equipment slots
            switch (acceptedItemStackTypes[0]) { // Just get the first accepted item to generate the equipment image
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
                case RING:
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
        }
    }

    /**
     * Completely removes the {@link ItemStack} in this {@link ItemStackSlot}
     */
    void deleteStack() {
        itemStack = null;
    }

    /**
     * Displays an image that represents an empty {@link ItemStackSlot}
     */
    void setEmptyCellImage() {
        if (itemStackImage != null) itemStackImage.remove();
        if (emptyCellImage == null) initEmptyCellImage(); // Equipment slot empty image
        add(emptyCellImage);
    }

    /**
     * Display the image that represents the current {@link ItemStack} in this {@link ItemStackSlot}
     */
    void setItemImage() {
        emptyCellImage.remove();
        add(itemStackImage);
        itemStackToolTip.updateToolTipText(itemStack);
    }

    /**
     * Places the {@link ItemStack} into this {@link ItemStackSlot}
     *
     * @param itemStack The {@link ItemStack} that is being set into this {@link ItemStackSlot}
     */
    void setItemStack(ItemStack itemStack) {
        if (itemStackImage != null) itemStackImage.remove();
        this.itemStack = itemStack;
        emptyCellImage.remove();
        itemStackImage = new ImageBuilder(GameAtlas.ITEMS, 32).setRegionName(itemStack.getTextureRegion()).buildVisImage();
        add(itemStackImage);
        itemStackToolTip.updateToolTipText(itemStack);
        addToolTipListener();
    }

    private Vector2 getStageLocation(Actor actor) {
        return actor.localToStageCoordinates(localCords.set(0, 0));
    }

    /**
     * Adds a ToolTip for an {@link ItemStack}. ToolTips contain information about the {@link ItemStack}.
     */
    private void addToolTipListener() {
        itemStackImage.addListener(new InputListener() {

            /**
             * Called when a mouse button or a finger touch goes down on the actor. If true is returned, this listener will receive all
             * touchDragged and touchUp events, even those not over this actor, until touchUp is received. Also when true is returned, the
             * event is {@link Event#handle() handled}.
             *
             * @see InputEvent
             */
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                // Shift + Left click
                if (button == Input.Buttons.LEFT && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    println(ItemStackSlot.class, "SHIFT + LEFT CLICK");
                    return true;
                }
                // Shift + Right click
                if (button == Input.Buttons.RIGHT && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    println(ItemStackSlot.class, "SHIFT + LEFT CLICK");
                    return true;
                }
                return false;
            }

            /** Called any time the mouse cursor or a finger touch is moved over an actor. On the desktop, this event occurs even when no
             * mouse buttons are pressed (pointer will be -1).
             * @param fromActor May be null.
             * @see InputEvent */
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (itemStackToolTip.isVisible()) return;
                stageLocation = getStageLocation(itemStackSlot);
                itemStackToolTip.toFront();
                itemStackToolTip.fadeIn().setVisible(true);

                // Setting X location
                if (stageLocation.x > Gdx.graphics.getWidth() / 2) {
                    itemStackToolTip.setX(stageLocation.x - itemStackToolTip.getWidth());
                } else {
                    itemStackToolTip.setX(stageLocation.x + itemStackSlot.getWidth());
                }

                // Setting Y location
                if (stageLocation.y > Gdx.graphics.getHeight() / 2) {
                    itemStackToolTip.setY(stageLocation.y - itemStackToolTip.getHeight());
                } else {
                    itemStackToolTip.setY(stageLocation.y + itemStackSlot.getHeight());
                }
            }

            /** Called any time the mouse cursor or a finger touch is moved out of an actor. On the desktop, this event occurs even when no
             * mouse buttons are pressed (pointer will be -1).
             * @param toActor May be null.
             * @see InputEvent */
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (itemStackToolTip.isVisible()) itemStackToolTip.fadeOut();
            }
        });
    }
}
