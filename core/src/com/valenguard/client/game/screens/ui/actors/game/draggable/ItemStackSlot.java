package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.inventory.InventoryType;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;
import lombok.Setter;

public class ItemStackSlot extends VisTable implements Buildable {

    /**
     * Used to places images in the UserInterface
     */
    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    /**
     * The slot index of this {@link ItemStackSlot}. This ID is used as locational data
     * for the server and the client.
     */
    @Getter
    private byte slotIndex;

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
     * A label that represents the amount of the {@link ItemStack}
     */
    private VisLabel amountLabel = new VisLabel();
    private Stack stack = new Stack();

    /**
     * If this slot is locked, prevent any and all changes to it!
     */
    @Getter
    private boolean tradeSlotLocked = false;

    @Getter
    @Setter
    private boolean moveSlotLocked = false;

    /**
     * Shows information about this {@link ItemStackSlot}
     */
    private ItemStackToolTip itemStackToolTip;

    private InputListener clickListener;

    private ItemSlotContainer itemSlotContainer;

    ItemStackSlot(ItemSlotContainer itemSlotContainer, InventoryType inventoryType, byte slotIndex) {
        this.itemSlotContainer = itemSlotContainer;
        this.inventoryType = inventoryType;
        this.slotIndex = slotIndex;
    }

    ItemStackSlot(ItemSlotContainer itemSlotContainer, byte slotIndex, ItemStackType[] acceptedItemStackTypes) {
        this.itemSlotContainer = itemSlotContainer;
        this.inventoryType = InventoryType.EQUIPMENT;
        this.slotIndex = slotIndex;
        this.acceptedItemStackTypes = acceptedItemStackTypes;
    }

    @Override
    public Actor build() {
        if (itemStack == null) {
            setEmptyCellImage();
        } else {
            setItemStack(itemStack);
        }
        add(stack);
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
    public void setEmptyCellImage() {
        if (itemStackImage != null) itemStackImage.remove();
        if (emptyCellImage == null) initEmptyCellImage(); // Equipment slot empty image
        if (itemStackToolTip != null) {
            itemStackToolTip.unregisterToolTip();
            itemStackToolTip = null;
        }
        amountLabel.remove();
        stack.add(emptyCellImage);
    }

    /**
     * Display the image that represents the current {@link ItemStack} in this {@link ItemStackSlot}
     */
    void setItemImage() {
        emptyCellImage.remove();
        amountLabel.remove();
        stack.add(itemStackImage);

        // Add item amount
        if (itemStack.getStackable() > 0) {
            displayItemAmount();
        } else {
            amountLabel.setText("");
        }

        if (itemStackToolTip != null) {
            itemStackToolTip.unregisterToolTip();
            itemStackToolTip = null;
            itemStackToolTip = new ItemStackToolTip(itemStack, itemStackImage);
            itemStackToolTip.registerToolTip();
        }
    }

    /**
     * Places the {@link ItemStack} into this {@link ItemStackSlot}
     *
     * @param itemStack The {@link ItemStack} that is being set into this {@link ItemStackSlot}
     */
    void setItemStack(ItemStack itemStack) {
        if (tradeSlotLocked) return;

        if (itemStackImage != null) itemStackImage.remove();
        this.itemStack = itemStack;
        emptyCellImage.remove();
        amountLabel.remove();
        itemStackImage = new ImageBuilder(GameAtlas.ITEMS, 32).setRegionName(itemStack.getTextureRegion()).buildVisImage();
        stack.add(itemStackImage);

        // Setup tool tip
        if (itemStackToolTip != null) {
            itemStackToolTip.unregisterToolTip();
            itemStackToolTip = null;
        }
        itemStackToolTip = new ItemStackToolTip(itemStack, itemStackImage);
        itemStackToolTip.registerToolTip();

        // Add item amount
        if (itemStack.getStackable() > 0) {
            displayItemAmount();
        } else {
            amountLabel.remove();
        }

        // Setup click listener
        addClickListener(itemStack, this);
    }

    private void displayItemAmount() {
        if (itemStack.getAmount() <= 1) return;
        int itemStackAmount = itemStack.getAmount();
        String displayText = String.valueOf(itemStackAmount);
        if (itemStackAmount >= 100000 && itemStackAmount < 1000000) {
            displayText = String.valueOf(itemStackAmount / 1000) + "K";
        } else if (itemStackAmount >= 1000000) {
            displayText = String.valueOf(itemStackAmount / 1000000) + "M";
        }

        amountLabel.setText(displayText);
        amountLabel.setAlignment(Alignment.BOTTOM_RIGHT.getAlignment());
        stack.add(amountLabel);
    }

    public void toggleLockedSlot(boolean lockThisSlot) {
        this.tradeSlotLocked = lockThisSlot;
        if (lockThisSlot) {
            itemStackImage.setColor(new Color(1, 0f, 0f, .5f));
        } else {
            itemStackImage.setColor(new Color(1, 1, 1, 1f));
        }
    }

    private void addClickListener(final ItemStack itemStack, final ItemStackSlot itemStackSlot) {
        if (clickListener != null) removeListener(clickListener);
        stack.addListener(clickListener = new InputListener() {

            /** Called when a mouse button or a finger touch goes down on the actor. If true is returned, this listener will receive all
             * touchDragged and touchUp events, even those not over this actor, until touchUp is received. Also when true is returned, the
             * event is {@link Event#handle() handled}.
             * @see InputEvent */
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (tradeSlotLocked) return true;

                // Trade Item click
                if (ActorUtil.getStageHandler().getTradeWindow().isVisible()) {
                    if (itemStack == null) return true; // Empty slot click!

                    ActorUtil.getStageHandler().getTradeWindow().addItemFromInventory(itemStack, inventoryType, itemStackSlot);
                    return true;
                }

                // Shift + Left click
                if (button == Input.Buttons.LEFT && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    return true;
                }
                // Shift + Right click
                if (button == Input.Buttons.RIGHT && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    return true;
                }

                if (button == Input.Buttons.RIGHT) {
                    // Bringing up options for the item!
                    if (itemStack != null) {
                        ActorUtil.getStageHandler().getItemDropDownMenu().toggleMenu(itemStack, inventoryType, itemStackSlot, slotIndex,
                                itemSlotContainer.getX() + itemStackSlot.getX() + x,
                                itemSlotContainer.getY() + itemStackSlot.getY() + y);
                    }
                    return true;
                }

                return false;
            }
        });
    }
}
