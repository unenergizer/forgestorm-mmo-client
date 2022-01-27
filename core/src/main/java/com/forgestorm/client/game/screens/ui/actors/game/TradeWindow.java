package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemSlotInterfaceUtil;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemStackSlot;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.ItemStackToolTip;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.client.game.world.item.trade.TradeManager;
import com.forgestorm.client.game.world.item.trade.TradePacketInfoOut;
import com.forgestorm.client.network.game.packet.out.PlayerTradePacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.inventory.InventoryConstants;
import com.forgestorm.shared.game.world.item.inventory.InventoryType;
import com.forgestorm.shared.game.world.item.trade.TradeStatusOpcode;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;
import static com.forgestorm.client.util.Preconditions.checkNotNull;

public class TradeWindow extends HideableVisWindow implements Buildable {

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    private final TradeWindowSlot[] playerClientTradeSlots = new TradeWindowSlot[InventoryConstants.BAG_SIZE];
    private final TradeWindowSlot[] targetPlayerTradeSlots = new TradeWindowSlot[InventoryConstants.BAG_SIZE];

    private final VisLabel playerTradeStatus = new VisLabel();
    private final VisLabel targetTradeStatus = new VisLabel();

    private StageHandler stageHandler;
    private TradeManager tradeManager;

    private TextButton accept;
    private TextButton cancel;

    private boolean lockTrade = false;
    private boolean tradeWindowOpen = false;

    @Setter
    @Getter
    private Player tradeTarget;

    public TradeWindow() {
        super("Trade Window");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;

        TableUtils.setSpacingDefaults(this);
        setResizable(false);

        tradeManager = ClientMain.getInstance().getTradeManager();

        // Setup notify table
        VisTable statusTable = new VisTable();

        playerTradeStatus.setText("[RED]You have not confirmed.");
        targetTradeStatus.setText("[RED]Target not confirmed.");


        playerTradeStatus.setAlignment(Alignment.CENTER.getAlignment());
        targetTradeStatus.setAlignment(Alignment.CENTER.getAlignment());

        statusTable.add(playerTradeStatus).expand().fill();
        statusTable.add(targetTradeStatus).expand().fill();

        add(statusTable).expand().fill().row();

        /*
         * Setup trade slots
         */
        VisTable tradeWindow = new VisTable();

        // Client player trade slots
        VisTable playerClientTable = new VisTable();
        buildTradeWindowSlot(playerClientTradeSlots, playerClientTable, true);

        // Networked player (other player) trade slots
        VisTable targetPlayerTable = new VisTable();
        buildTradeWindowSlot(targetPlayerTradeSlots, targetPlayerTable, false);

        // Add both trade window to the main window
        tradeWindow.add(playerClientTable).padRight(5);
        tradeWindow.add(targetPlayerTable);
        add(tradeWindow).row();

        /*
         * Setup Trade Buttons
         */
        VisTable buttonArea = new VisTable();

        accept = new TextButton("Accept", VisUI.getSkin());
        cancel = new TextButton("Cancel", VisUI.getSkin());

        buttonArea.add(accept).expand().fill();
        buttonArea.add(cancel).expand().fill();

        add(buttonArea).expand().fill();

        accept.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(TradeWindow.class, (short) 0);
                if (!lockTrade) {
                    // First accept check (trade confirmed)
                    new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_OFFER_CONFIRM, tradeManager.getTradeUUID())).sendPacket();
                }
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(TradeWindow.class, (short) 0);
                if (!lockTrade) {
                    new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_CANCELED, tradeManager.getTradeUUID())).sendPacket();
                } else if (lockTrade) {
                    new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_OFFER_UNCONFIRM, tradeManager.getTradeUUID())).sendPacket();
                }
            }
        });

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_CANCELED, tradeManager.getTradeUUID())).sendPacket();
                closeTradeWindow();
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        pack();
        setVisible(false);
        return this;
    }

    public void confirmTradeUI(short playerUUID) {
        if (EntityManager.getInstance().getPlayerClient().getServerEntityID() == playerUUID) {
            // Player client confirmed trade, lock our left pane.

            println(getClass(), "PlayerClient has confirmed the trade!", true);

            lockTrade = true;
            accept.setText("Trade Confirmed");
            accept.setDisabled(true);
            cancel.setText("Cancel Confirmation");

            playerTradeStatus.setText("[GREEN]You Confirmed!");
            ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 0);
        } else {
            targetTradeStatus.setText("[GREEN]Target Confirmed!");
            ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 16);
        }
    }

    public void unconfirmTradeUI(short playerUUID) {
        if (EntityManager.getInstance().getPlayerClient().getServerEntityID() == playerUUID) {
            // Player client confirmed trade, lock our left pane.

            println(getClass(), "PlayerClient has unconfirmed the trade!", true);

            lockTrade = false;
            accept.setText("Accept");
            accept.setDisabled(false);
            cancel.setText("Cancel");

            playerTradeStatus.setText("[RED]You have not confirmed.");
            ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 0);
        } else {
            targetTradeStatus.setText("[RED]Target not confirmed.");
            ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(getClass(), (short) 18);
        }
    }

    /**
     * Builds a slot to go inside this {@link TradeWindow}
     *
     * @param targetSlot The array of slots to build a slot for.
     * @param visTable   The table we will add the slot to.
     */
    private void buildTradeWindowSlot(TradeWindowSlot[] targetSlot, VisTable visTable, boolean isClientPlayerSlot) {
        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BAG_SIZE; i++) {

            TradeWindowSlot tradeWindowSlot = new TradeWindowSlot(i, isClientPlayerSlot);
            visTable.add(tradeWindowSlot);

            targetSlot[i] = tradeWindowSlot;
            columnCount++;

            if (columnCount == InventoryConstants.BAG_WIDTH) {
                visTable.row();
                columnCount = 0;
            }
        }
    }

    /**
     * Adds an {@link ItemStack} to the {@link TradeWindow}
     *
     * @param itemStack           The {@link ItemStack} the player wants to trade
     * @param lockedItemStackSlot If not null, toggle a lock on this slot to prevent
     *                            changes to it.
     */
    public void addItemFromInventory(ItemStack itemStack, InventoryType inventoryType, ItemStackSlot lockedItemStackSlot) {
        if (inventoryType == InventoryType.BANK) return; // Bank click!
        if (inventoryType == InventoryType.EQUIPMENT) return; // Equipment click!
        if (inventoryType == InventoryType.HOT_BAR) return; // HotBar click!

        if (lockTrade) return; // Trade accepted, waiting on final confirm
        if (!tradeWindowOpen) return; // Trade window is closed or fading out. Do not add items.

        // Find an empty trade slot
        TradeWindowSlot tradeWindowSlot = findEmptySlot(true);

        if (tradeWindowSlot == null) return; // Deny item placement

        tradeWindowSlot.setTradeCell(itemStack, lockedItemStackSlot);
        checkNotNull(lockedItemStackSlot, "This can never be null!");
        lockedItemStackSlot.toggleLockedSlot(true);

        println(getClass(), "Slot index being sent = " + lockedItemStackSlot.getSlotIndex());

        new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_ITEM_ADD, tradeManager.getTradeUUID(), lockedItemStackSlot.getSlotIndex())).sendPacket();

        // Play sound of the item clicked
        ClientMain.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), itemStack);
    }

    public void addItemFromPacket(int itemStackUUID, int itemAmount) {
        // Find an empty trade slot
        ItemStack itemStack = ClientMain.getInstance().getItemStackManager().makeItemStack(itemStackUUID, itemAmount);
        TradeWindowSlot tradeWindowSlot = findEmptySlot(false);
        tradeWindowSlot.setTradeCell(itemStack, null);

        // Play sound of item added
        ClientMain.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), itemStack);
    }

    public void removeItemFromPacket(byte itemSlot) {
        TradeWindowSlot tradeWindowSlot = targetPlayerTradeSlots[itemSlot];

        // Find an empty trade slot
        tradeWindowSlot.setTradeCell(null, null);
    }

    /**
     * Finds an empty slot to place a {@link ItemStack} in
     *
     * @param isClientPlayer Determines which trade slots to use
     * @return A {@link TradeWindowSlot} if one could be found
     */
    private TradeWindowSlot findEmptySlot(boolean isClientPlayer) {
        if (isClientPlayer) {
            for (TradeWindowSlot windowSlot : playerClientTradeSlots) {
                if (windowSlot.itemStack == null) return windowSlot;
            }
        } else {
            for (TradeWindowSlot windowSlot : targetPlayerTradeSlots) {
                if (windowSlot.itemStack == null) return windowSlot;
            }
        }
        return null;
    }

    public void closeTradeWindow() {
        tradeWindowOpen = false;
        ActorUtil.fadeOutWindow(this);
        fadeOut();
        lockTrade = false;
        accept.setText("Accept");
        accept.setDisabled(false);
        cancel.setText("Cancel");

        playerTradeStatus.setText("[RED]You have not confirmed.");
        targetTradeStatus.setText("[RED]Target not confirmed.");
        clearTradeSlots();
    }

    public void openWindow() {
        clearTradeSlots();
        ActorUtil.fadeInWindow(this);
        centerWindow();
        tradeWindowOpen = true;
    }

    private void clearTradeSlots() {
        // Reset trade slots
        for (TradeWindowSlot tradeWindowSlot : playerClientTradeSlots) {
            tradeWindowSlot.setTradeCell(null, null);
        }
        for (TradeWindowSlot tradeWindowSlot : targetPlayerTradeSlots) {
            tradeWindowSlot.setTradeCell(null, null);
        }
    }

    /**
     * This class holds information for a particular {@link TradeWindowSlot}
     * within a {@link TradeWindow}
     */
    private class TradeWindowSlot extends VisTable {

        /**
         * The slot index for this slot...
         */
        private final byte slotIndex;

        /**
         * Used to make sure the player can't remove ItemStacks from the targetPlayers panel
         */
        private final boolean isClientPlayerSlot;

        /**
         * The following two vars are used to build the slots display image
         */
        private VisImage tradeCell;
        private ItemStack itemStack;

        private final VisLabel amountLabel = new VisLabel();
        private final Stack stack = new Stack();

        /**
         * We declare this when a {@link ItemStack} needs to be locked in place on the players bag.
         */
        private ItemStackSlot lockedItemStackSlot;

        private ItemStackToolTip itemStackToolTip;

        private InputListener clickListener;

        TradeWindowSlot(final byte slotIndex, final boolean isClientPlayerSlot) {
            this.slotIndex = slotIndex;
            this.isClientPlayerSlot = isClientPlayerSlot;

            add(stack);
            setTradeCell(null, null); // Init empty slot
        }

        /**
         * Sets the trade cell image
         *
         * @param itemStack           The {@link ItemStack} we are adding to this slot
         * @param lockedItemStackSlot If not null, toggle a lock on this slot to prevent changes to it.
         */
        void setTradeCell(ItemStack itemStack, ItemStackSlot lockedItemStackSlot) {
            if (tradeCell != null) tradeCell.remove(); // Remove current image

            if (itemStack != null) {
                /*
                 * Here were setting up the slot and locking the ItemStackSlot
                 */
                this.itemStack = itemStack;
                tradeCell = imageBuilder.setRegionName(itemStack.getTextureRegion()).buildVisImage();

                if (lockedItemStackSlot != null) {
                    this.lockedItemStackSlot = lockedItemStackSlot;
                    this.lockedItemStackSlot.toggleLockedSlot(true);
                }
            } else {

                /*
                 * Were clearing the slot here and unlocking the ItemStackSlot
                 */
                this.itemStack = null;
                tradeCell = imageBuilder.setRegionName("clear").buildVisImage();

                if (this.lockedItemStackSlot != null) {
                    this.lockedItemStackSlot.toggleLockedSlot(false);
                    this.lockedItemStackSlot = null;
                }
            }

            stack.add(tradeCell); // Set next image
            if (itemStack != null) {
                ItemSlotInterfaceUtil.displayItemAmount(itemStack, amountLabel, stack);
            } else {
                stack.removeActor(amountLabel);
            }

            // Setup tool tips
            if (itemStack != null && tradeCell != null) {
                if (itemStackToolTip != null) {
                    itemStackToolTip.unregisterToolTip();
                    itemStackToolTip = null;
                }
                itemStackToolTip = new ItemStackToolTip(stageHandler, lockedItemStackSlot, itemStack, tradeCell, false);
                itemStackToolTip.registerToolTip();
            }

            // Setup click listener
            addClickListener();
        }

        void addClickListener() {
            if (clickListener != null) removeListener(clickListener);
            stack.addListener(clickListener = new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(TradeWindowSlot.class, (short) 0);
                    if (itemStack != null && isClientPlayerSlot && !lockTrade) {
                        println(getClass(), "removed item from trade window");

                        // Send other player info that we are removing an item from trade
                        new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_ITEM_REMOVE, tradeManager.getTradeUUID(), slotIndex)).sendPacket();

                        // Remove trade item and gameQuitReset the slot image
                        setTradeCell(null, null);
                    }
                    return true;
                }
            });
        }
    }
}