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
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.inventory.InventoryType;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;
import lombok.Setter;

public class ItemStackSlot extends VisTable {

    /**
     * Used to places images in the UserInterface
     */
    private final ImageBuilder imageBuilder;

    @Getter
    private final Color emptySlotColor = new Color(1f, 1f, 1f, .2f);

    private final float iconSize;

    private StageHandler stageHandler;

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
    private VisLabel countdownLabel = new VisLabel();
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
     * If true, then this slot is used for the {@link CharacterInspectionWindow}.
     * Since it's an inspection window, we don't want to allow the player to
     * drag an {@link ItemStack} from the window. This makes the slot for display
     * only.
     */
    @Getter
    @Setter
    private boolean characterInspectionSlot = false;

    /**
     * Shows information about this {@link ItemStackSlot}
     */
    private ItemStackToolTip itemStackToolTip;

    private InputListener clickListener;

    @Getter
    private ItemSlotContainer itemSlotContainer;

    @Getter
    private boolean magicItemActivated = false;

    ItemStackSlot(ItemSlotContainer itemSlotContainer, InventoryType inventoryType, float iconSize, byte slotIndex) {
        this.itemSlotContainer = itemSlotContainer;
        this.inventoryType = inventoryType;
        this.slotIndex = slotIndex;
        this.iconSize = iconSize;
        this.imageBuilder = new ImageBuilder(GameAtlas.ITEMS, iconSize);
    }

    ItemStackSlot(ItemSlotContainer itemSlotContainer, float iconSize, byte slotIndex, ItemStackType[] acceptedItemStackTypes) {
        this.itemSlotContainer = itemSlotContainer;
        this.inventoryType = InventoryType.EQUIPMENT;
        this.iconSize = iconSize;
        this.slotIndex = slotIndex;
        this.acceptedItemStackTypes = acceptedItemStackTypes;
        this.imageBuilder = new ImageBuilder(GameAtlas.ITEMS, iconSize);
    }

    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;

        // Add ItemStack cell background
        VisTable visTable = new VisTable();
        visTable.setBackground(stageHandler.getItemStackCellBackground());
        stack.add(visTable);

        // Add ItemStack image or blank image to the cell
        if (itemStack == null) {
            setEmptyCellImage();
        } else {
            setItemStack(itemStack);
        }

        addClickListener(this);

        // Add the stack to this table!
        add(stack).padLeft(2).padTop(2);
        return this;
    }

    /**
     * An ability or magical item that has just been activated.
     */
    public void activateItemStack() {
        magicItemActivated = true;
        itemStackImage.setColor(Color.DARK_GRAY);
        countdownLabel.setAlignment(Alignment.CENTER.getAlignment());
        stack.add(countdownLabel);
    }

    /**
     * An ability or magical item that has just finished cooling down.
     */
    public void resetItemStack() {
        magicItemActivated = false;
        itemStackImage.setColor(Color.WHITE);
        stack.removeActor(countdownLabel);
    }

    /**
     * Updates the remaining countdown of the magic item.
     */
    public void updateCountdown(int timeRemaining) {
        // TODO: Figure out how to cleanly show seconds and milliseconds remaining
//        float time = (float) timeRemaining / 60;
//        countdownLabel.setText(StringUtils.abbreviateString(Float.toString(time), 4));
        countdownLabel.setText(timeRemaining);
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
                case PANTS:
                    emptyCellImage = imageBuilder.setRegionName("pants_01").buildVisImage();
                    break;
                case SHOES:
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
            // Fades out the image for the empty cell. Primarily used by EquipmentBag to
            // indicate an empty ItemStack cell.
            emptyCellImage.setColor(emptySlotColor);
        }
    }

    /**
     * Completely removes the {@link ItemStack} in this {@link ItemStackSlot}
     */
    void resetItemStackSlot() {
        itemStack = null;
        setEmptyCellImage();
        if (itemStackToolTip != null) {
            itemStackToolTip.unregisterToolTip();
            itemStackToolTip = null;
        }
    }

    /**
     * Displays an image that represents an empty {@link ItemStackSlot}
     */
    void setEmptyCellImage() {
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
        if (itemStack.getStackable() > 1) {
            displayItemAmount();
        } else {
            amountLabel.setText("");
        }

        if (itemStackToolTip != null) {
            itemStackToolTip.unregisterToolTip();
            itemStackToolTip = null;
            itemStackToolTip = new ItemStackToolTip(stageHandler, this, itemStack, itemStackImage, !characterInspectionSlot);
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
        itemStackImage = new ImageBuilder(GameAtlas.ITEMS, iconSize).setRegionName(itemStack.getTextureRegion()).buildVisImage();
        stack.add(itemStackImage);

        // Setup tool tip
        if (itemStackToolTip != null) {
            itemStackToolTip.unregisterToolTip();
            itemStackToolTip = null;
        }
        itemStackToolTip = new ItemStackToolTip(stageHandler, this, itemStack, itemStackImage, !characterInspectionSlot);
        itemStackToolTip.registerToolTip();

        // Add item amount
        if (itemStack.getStackable() > 1) {
            displayItemAmount();
        } else {
            amountLabel.remove();
        }
    }

    private void displayItemAmount() {
        if (itemStack.getAmount() <= 1) return;
        int itemStackAmount = itemStack.getAmount();
        String displayText = String.valueOf(itemStackAmount);
        if (itemStackAmount >= 100000 && itemStackAmount < 1000000) {
            displayText = itemStackAmount / 1000 + "K";
        } else if (itemStackAmount >= 1000000) {
            displayText = itemStackAmount / 1000000 + "M";
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

    void highlightSlot(Color color) {
        if (itemStack != null) {
            itemStackImage.setColor(color);
        } else {
            emptyCellImage.setColor(color);
        }
    }

    private void addClickListener(final ItemStackSlot itemStackSlot) {
        if (clickListener != null) removeListener(clickListener);
        stack.addListener(clickListener = new InputListener() {

            /** Called when a mouse button or a finger touch goes down on the actor. If true is returned, this listener will receive all
             * touchDragged and touchUp events, even those not over this actor, until touchUp is received. Also when true is returned, the
             * event is {@link Event#handle() handled}.
             * @see InputEvent */
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (characterInspectionSlot) return true;
                if (tradeSlotLocked) return true;
                if (itemStack == null) return true; // Empty slot click!

                // Trade Item click
                if (stageHandler.getTradeWindow().isVisible()) {

                    stageHandler.getTradeWindow().addItemFromInventory(itemStack, inventoryType, itemStackSlot);
                    return true;
                }

                ItemSlotContainer bagContainer = stageHandler.getBagWindow().getItemSlotContainer();
                ItemSlotContainer bankContainer = stageHandler.getBankWindow().getItemSlotContainer();
                ItemSlotContainer hotBarContainer = stageHandler.getHotBar().getItemSlotContainer();
                boolean isBankOpen = stageHandler.getBankWindow().isVisible();
                boolean isBagOpen = stageHandler.getBagWindow().isVisible();

                // Shift + Left or Shift + Right click
                if ((button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {

                    // Prevent the movement of BOOK_SKILL items
                    if (itemStack.getItemStackType() == ItemStackType.BOOK_SKILL) {
                        stageHandler.getChatWindow().appendChatMessage("[RED] This can not be moved to your inventory.");
                        return true;
                    }

                    // Note: InventoryType is the container we are clicking in.
                    if (inventoryType == InventoryType.BAG_1) {
                        if (isBankOpen) {
                            bagContainer.swapInventories(itemStack, itemStackSlot, bankContainer);
                        } else {
                            bagContainer.swapInventories(itemStack, itemStackSlot, hotBarContainer);
                        }
                    } else if (inventoryType == InventoryType.HOT_BAR) {
                        if (isBankOpen) {
                            hotBarContainer.swapInventories(itemStack, itemStackSlot, bankContainer);
                        } else {
                            hotBarContainer.swapInventories(itemStack, itemStackSlot, bagContainer);
                        }
                    } else if (inventoryType == InventoryType.BANK) {
                        if (isBagOpen) {
                            bankContainer.swapInventories(itemStack, itemStackSlot, bagContainer);
                        } else {
                            bankContainer.swapInventories(itemStack, itemStackSlot, hotBarContainer);
                        }
                    }
                    return true;
                }

                // CTRL + Left or Shift + Right click
                if ((button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {

                    // Prevent the movement of BOOK_SKILL items
                    if (itemStack.getItemStackType() == ItemStackType.BOOK_SKILL) {
                        stageHandler.getChatWindow().appendChatMessage("[RED] This can not be moved to your inventory.");
                        return true;
                    }

                    if (!itemStack.getItemStackType().isEquipable()) return true;

                    EquipmentWindow equipmentWindow = stageHandler.getEquipmentWindow();
                    if (inventoryType == InventoryType.BAG_1
                            || inventoryType == InventoryType.BANK
                            || inventoryType == InventoryType.HOT_BAR) {
                        equipmentWindow.equipItem(itemStack, itemStackSlot);
                    } else if (inventoryType == InventoryType.EQUIPMENT) {
                        boolean isHotBarFull = hotBarContainer.isInventoryFull(itemStack);
                        if (!isBagOpen && !isHotBarFull) {
                            equipmentWindow.unequipItem(hotBarContainer, itemStack, itemStackSlot);
                        } else {
                            equipmentWindow.unequipItem(bagContainer, itemStack, itemStackSlot);
                        }
                    }
                    return true;
                }

                // Do Magic Ability
                if (button == Input.Buttons.LEFT) {
                    if (itemStack.getItemStackType() == ItemStackType.BOOK_SKILL || itemStack.getSkillID() != null) {
                        itemSlotContainer.magicItemInteract(itemStackSlot, itemStack);
                        return true;
                    }
                }

                // Show drop down menu
                if (button == Input.Buttons.RIGHT) {
                    // Bringing up options for the item!
                    stageHandler.getItemDropDownMenu().toggleMenu(itemStack, inventoryType, itemStackSlot, slotIndex,
                            itemSlotContainer.getParentActor().getX() + itemStackSlot.getX() + x,
                            itemSlotContainer.getParentActor().getY() + itemStackSlot.getY() + y);

                    return true;
                }

                return false;
            }
        });
    }
}
