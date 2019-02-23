package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientConstants;
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

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    private final TradeWindowSlot[] playerClientTradeSlots = new TradeWindowSlot[ClientConstants.BAG_SIZE];
    private final TradeWindowSlot[] targetPlayerTradeSlots = new TradeWindowSlot[ClientConstants.BAG_SIZE];

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
        buildTradeWindowSlot(playerClientTradeSlots, playerClientTable, true);

        // Networked player (other player) trade slots
        VisTable targetPlayerTable = new VisTable();
        buildTradeWindowSlot(targetPlayerTradeSlots, targetPlayerTable, false);

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
        for (byte i = 0; i < ClientConstants.BAG_SIZE; i++) {

            TradeWindowSlot tradeWindowSlot = new TradeWindowSlot(i, isClientPlayerSlot);
            tradeWindowSlot.buildSlot();

            visTable.add(tradeWindowSlot);

            targetSlot[i] = tradeWindowSlot;
            columnCount++;

            if (columnCount == ClientConstants.BAG_WIDTH) {
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
     * @return True if the item could be place, false otherwise.
     */
    public boolean addItemFromInventory(ItemStack itemStack, ItemStackSlot lockedItemStackSlot) {
        if (lockTrade) return false; // Trade accepted, waiting on final confirm

        // Find an empty trade slot
        TradeWindowSlot tradeWindowSlot = findEmptySlot(true);

        if (tradeWindowSlot == null) return false; // Deny item placement

        tradeWindowSlot.setTradeCell(itemStack, lockedItemStackSlot);
        checkNotNull(lockedItemStackSlot, "This can never be null!");
        lockedItemStackSlot.toggleLockedSlot(true);

        println(getClass(), "Slot index being sent = " + lockedItemStackSlot.getInventoryIndex());

        new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_ITEM_ADD, tradeManager.getTradeUUID(), lockedItemStackSlot.getInventoryIndex())).sendPacket();

        return true; // Allow item placement
    }

    public void addItemFromPacket(int itemStackUUID) {
        // Find an empty trade slot
        ItemStack itemStack = Valenguard.getInstance().getItemManager().makeItemStack(itemStackUUID, 1);
        TradeWindowSlot tradeWindowSlot = findEmptySlot(false);
        tradeWindowSlot.setTradeCell(itemStack, null);
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

    /**
     * Finds an occupied slot to remove a {@link ItemStack} from
     *
     * @param isClientPlayer Determines which trade slots to use
     * @return A {@link TradeWindowSlot} if one could be found
     */
    private TradeWindowSlot findItemSlot(boolean isClientPlayer, int itemStackUUID) {
        if (isClientPlayer) {
            for (TradeWindowSlot windowSlot : playerClientTradeSlots) {
                if (windowSlot.itemStack.getItemId() == itemStackUUID) return windowSlot;
            }
        } else {
            for (TradeWindowSlot windowSlot : targetPlayerTradeSlots) {
                if (windowSlot.itemStack.getItemId() == itemStackUUID) return windowSlot;
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

        /**
         * We declare this when a {@link ItemStack} needs to be locked in place on the players bag.
         */
        private ItemStackSlot lockedItemStackSlot;

        TradeWindowSlot(final byte slotIndex, final boolean isClientPlayerSlot) {
            this.slotIndex = slotIndex;
            this.isClientPlayerSlot = isClientPlayerSlot;
        }

        /**
         * Builds a new window slot
         */
        void buildSlot() {
            setTradeCell(null, null); // Init empty slot

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (itemStack != null && isClientPlayerSlot) {
                        // Send other player info that we are removing an item from trade
                        new PlayerTradePacketOut(new TradePacketInfoOut(TradeStatusOpcode.TRADE_ITEM_REMOVE, tradeManager.getTradeUUID(), slotIndex)).sendPacket();

                        // Remove trade item and reset the slot image
                        setTradeCell(null, null);
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
