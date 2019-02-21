package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.inventory.TradeManager;
import com.valenguard.client.game.inventory.TradePacketInfoOut;
import com.valenguard.client.game.inventory.TradeStatusOpcode;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackSlot;
import com.valenguard.client.network.packet.out.PlayerTradePacketOut;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;
import static com.valenguard.client.util.Preconditions.checkNotNull;

public class TradeWindow extends HideableVisWindow implements Buildable {

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 5;
    private static final int FINAL_SIZE = NUM_ROWS * NUM_COLUMNS;

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    private final TradeWindowSlot[] playerClientTradeSlots = new TradeWindowSlot[FINAL_SIZE];
    private final TradeWindowSlot[] targetPlayerTradeSlots = new TradeWindowSlot[FINAL_SIZE];

    private TradeManager tradeManager;

    private TextButton accept;
    private TextButton cancel;

    private boolean lockTrade = false;

    @Setter
    @Getter
    private MovingEntity targetPlayer;

    public TradeWindow() {
        super("Trade Window");
    }

    @Override
    public Actor build() {
        tradeManager = Valenguard.getInstance().getTradeManager();

        /*
         * Setup trade slots
         */
        VisTable tradeWindow = new VisTable();

        // Client player trade slots
        VisTable playerClientTable = new VisTable();
        buildTradeWindowSlot(playerClientTradeSlots, playerClientTable);

        // Networked player (other player) trade slots
        VisTable targetPlayerTable = new VisTable();
        buildTradeWindowSlot(targetPlayerTradeSlots, targetPlayerTable);

        // Add both trade window to the main window
        tradeWindow.add(playerClientTable);
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
                if (!lockTrade) {
                    // First accept check (trade confirmed)
                    new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_OFFER_CONFIRM, tradeManager.getTradeUUID())).sendPacket();
                }
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_CANCELED, tradeManager.getTradeUUID())).sendPacket();
            }
        });

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true; // Prevent click-through
            }
        });

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

    public void setupConfirmButtons(short playerUUID) {

        if (EntityManager.getInstance().getPlayerClient().getServerEntityID() == playerUUID) {
            // Player client confirmed trade, lock our left pane.

            println(getClass(), "PlayerClient has confirmed the trade!", true);

            lockTrade = true;
            accept.setText("Confirm Trade");
            cancel.setText("Cancel Confirm");
        } else {
            MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(playerUUID);
            // Other player has confirmed trade, show some kind of status indicator
            // TODO: Show status indicator
            println(getClass(), "ENTITY NAME: " + movingEntity.getEntityName() + " UUID RECEIVED: " + playerUUID, true);
        }
    }

    /**
     * Builds a slot to go inside this {@link TradeWindow}
     *
     * @param targetSlot The array of slots to build a slot for.
     * @param visTable   The table we will add the slot to.
     */
    private void buildTradeWindowSlot(TradeWindowSlot[] targetSlot, VisTable visTable) {
        int columnCount = 0;
        for (byte i = 0; i < FINAL_SIZE; i++) {

            TradeWindowSlot tradeWindowSlot = new TradeWindowSlot(i);
            tradeWindowSlot.buildSlot();

            visTable.add(tradeWindowSlot);

            targetSlot[i] = tradeWindowSlot;
            columnCount++;

            if (columnCount == NUM_COLUMNS) {
                visTable.row();
                columnCount = 0;
            }
        }
    }

    /**
     * Adds an {@link ItemStack} to the {@link TradeWindow}
     *
     * @param itemStack           The {@link ItemStack} the player wants to trade
     * @param isClientPlayer      If true, this player wants to add an item. If
     *                            false the networked player wants to add an item.
     * @param lockedItemStackSlot If not null, toggle a lock on this slot to prevent
     *                            changes to it.
     * @return True if the item could be place, false otherwise.
     */
    public boolean addItem(ItemStack itemStack, boolean isClientPlayer, ItemStackSlot lockedItemStackSlot) {
        if (lockTrade) return false; // Trade accepted, waiting on final confirm

        // Find an empty trade slot
        TradeWindowSlot tradeWindowSlot = findEmptySlot(isClientPlayer);

        if (tradeWindowSlot == null) return false; // Deny item placement

        tradeWindowSlot.setTradeCell(itemStack, lockedItemStackSlot);

        if (isClientPlayer) {
            checkNotNull(lockedItemStackSlot, "This can never be null!");
            lockedItemStackSlot.toggleLockedSlot(true);
        }

        // TODO: Send ItemStack set in slot packet to targetPlayer

        return true; // Allow item placement
    }

    /**
     * Finds an empty slot to place a {@link ItemStack} in
     *
     * @param isClientPlayer Determines which trade slots to use
     * @return A {@link TradeWindowSlot} if an empty one could be found
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
        setVisible(false);
        lockTrade = false;
        accept.setText("Accept");
        cancel.setText("Cancel");

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
    class TradeWindowSlot extends VisTable {

        private final byte slotIndex;
        private VisImage tradeCell;
        private ItemStack itemStack;

        private ItemStackSlot lockedItemStackSlot;

        TradeWindowSlot(final byte slotIndex) {
            this.slotIndex = slotIndex;
        }

        /**
         * Builds a new window slot
         */
        void buildSlot() {
            setTradeCell(null, null); // Init empty slot

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (itemStack != null) {
                        println(getClass(), "[REMOVE ITEM] Clicked cell: " + slotIndex + " with Item: " + itemStack.getName());
                        setTradeCell(null, null); // Remove trade item and reset the slot image
                    } else {
                        println(getClass(), "[NO ITEM] Clicked cell: " + slotIndex + " with Item: null");
                    }
                    return true;
                }
            });
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

            add(tradeCell); // Set next image
        }
    }
}
