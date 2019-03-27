package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.EntityShopAction;
import com.valenguard.client.game.rpg.EntityShopManager;
import com.valenguard.client.game.rpg.ShopOpcodes;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackToolTip;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackManager;
import com.valenguard.client.game.world.item.inventory.ShopItemStackInfo;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.out.EntityShopPacketOut;

import java.util.ArrayList;
import java.util.List;

import static com.valenguard.client.util.Log.println;

public class EntityShopWindow extends HideableVisWindow implements Buildable {

    private static final int SHOP_WIDTH = 2;
    private static final int SHOP_HEIGHT = 6;
    private static final int SHOP_SIZE = SHOP_WIDTH * SHOP_HEIGHT;

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);
    private final VisLabel pageDisplay = new VisLabel();
    private final VisTextButton previousPage = new VisTextButton("Previous Page");
    private final VisTextButton nextPage = new VisTextButton("Next Page");
    private final VisTextButton exit = new VisTextButton("Exit Shop");

    private EntityShopWindow entityShopWindow;
    private VisTable pageContainer = new VisTable();
    private VisTable navTable = new VisTable();

    private List<VisTable> shopPages;
    private int currentPageIndex = 0;

    public EntityShopWindow() {
        super("Trade Shop");
    }

    @Override
    public Actor build() {
        entityShopWindow = this;
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(false);

        entityShopWindow.add(pageDisplay).row();

        navTable.add(previousPage).expand().fill();
        navTable.add(nextPage).expand().fill();
        navTable.row();
        navTable.add(exit).colspan(2).expand().fill();

        entityShopWindow.add(pageContainer).row();
        entityShopWindow.add(navTable).expand().fill();

        previousPage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (currentPageIndex > 0) {
                    currentPageIndex--;
                }

                setupButtons();
                changeShopPage(currentPageIndex);
            }
        });

        nextPage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (currentPageIndex < shopPages.size() - 1) {
                    currentPageIndex++;
                }

                setupButtons();
                changeShopPage(currentPageIndex);
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.STOP_SHOPPING)).sendPacket();
                ActorUtil.fadeOutWindow(entityShopWindow);
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

    private List<VisTable> buildShopPage(short shopID) {
        EntityShopManager entityShopManager = Valenguard.getInstance().getEntityShopManager();
        ItemStackManager itemStackManager = Valenguard.getInstance().getItemStackManager();

        // Generate shop slots
        List<EntityShopWindowSlot> entityShopWindowSlots = new ArrayList<EntityShopWindowSlot>();
        for (int i = 0; i < entityShopManager.getShopItemList(shopID).size(); i++) {
            ShopItemStackInfo shopItemStackInfo = entityShopManager.getShopItemStackInfo(shopID, i);
            ItemStack itemStack = itemStackManager.makeItemStack(entityShopManager.getItemIdForShop(shopID, i), 1);
            entityShopWindowSlots.add(new EntityShopWindowSlot(itemStack, shopItemStackInfo.getPrice(), (short) i));
        }

        // Generate shop pages
        List<VisTable> shopPageList = new ArrayList<VisTable>();
        int columnCount = 0;
        int pageCount = 0;
        VisTable shopPage = new VisTable();
        shopPageList.add(shopPage); // Add first page

        for (int i = 0; i < entityShopWindowSlots.size(); i++) {
            EntityShopWindowSlot entityShopWindowSlot = entityShopWindowSlots.get(i);
            entityShopWindowSlot.setItemStackCell();

            shopPage.add(entityShopWindowSlot).pad(5);
            columnCount++;
            pageCount++;

            // Test if we need to make a new page
            if (pageCount >= SHOP_SIZE) {
                // Start new page
                shopPage = new VisTable();
                shopPageList.add(shopPage);

                pageCount = 0;
                columnCount = 0;
            } else if (columnCount == SHOP_WIDTH) {
                // Test if we need to make a item row
                shopPage.row();
                columnCount = 0;
            }
        }

        // Generate blank spots
        int blankSpots = (shopPageList.size() * SHOP_SIZE) - entityShopWindowSlots.size();
        columnCount = entityShopWindowSlots.size() % SHOP_WIDTH;

        VisTable lastShopPage = shopPageList.get(shopPageList.size() - 1);

        for (int i = 0; i < blankSpots; i++) {
            EntityShopWindowSlot entityShopWindowSlot = new EntityShopWindowSlot(null, 0, (short) -1);
            entityShopWindowSlot.setItemStackCell();
            lastShopPage.add(entityShopWindowSlot);

            columnCount++;

            if (columnCount == SHOP_WIDTH) {
                // Test if we need to make a item row
                shopPage.row();
                columnCount = 0;
            }
        }
        return shopPageList;
    }

    private void changeShopPage(int pageIndex) {
        pageDisplay.setText("Page: " + (pageIndex + 1));

        // Reset content tables
        for (Actor actor : pageContainer.getChildren()) {
            actor.remove();
        }

        // Build item table
        pageContainer.add(shopPages.get(pageIndex));

        setupButtons();
        pack();
    }

    public void loadShop(MovingEntity movingEntity, short shopID) {
        resetShop();

        // Dynamic build shop pages
        shopPages = buildShopPage(shopID);
        println(getClass(), "Shop Pages: " + shopPages.size());
        changeShopPage(0);
        getTitleLabel().setText(movingEntity.getEntityName() + "'s Shop");
        ActorUtil.fadeInWindow(this);
    }

    private void setupButtons() {
        // Setup previous page
        if (currentPageIndex == 0) {
            previousPage.setDisabled(true);
        } else {
            previousPage.setDisabled(false);
        }

        // Setup
        if (currentPageIndex == shopPages.size() - 1) {
            nextPage.setDisabled(true);
        } else {
            nextPage.setDisabled(false);
        }
    }

    private void resetShop() {
        if (shopPages != null) shopPages.clear();
        shopPages = null;
        currentPageIndex = 0;
        previousPage.setDisabled(true);
        nextPage.setDisabled(false);
        getTitleLabel().setText("Shop Name: Null");
    }

    /**
     * This class holds information for a particular {@link EntityShopWindowSlot}
     * within a {@link EntityShopWindow}
     */
    private class EntityShopWindowSlot extends VisTable {

        /**
         * The {@link ItemStack} that is being sold by the vendor
         */
        private final ItemStack itemStack;

        /**
         * The price of the {@link ItemStack}
         */
        private final int price;

        /**
         * The image that represents this cell
         */
        private VisImage itemStackImage;

        private ItemStackToolTip itemStackToolTip;

        private short shopSlot;

        EntityShopWindowSlot(final ItemStack itemStack, final int price, final short shopSlot) {
            this.itemStack = itemStack;
            this.price = price;
            this.shopSlot = shopSlot;
        }

        /**
         * Sets cell image
         */
        private void setItemStackCell() {
            if (itemStack != null) {
                // Here were setting up the slot and locking the ItemStackSlot
                itemStackImage = imageBuilder.setRegionName(itemStack.getTextureRegion()).buildVisImage();
            } else {
                // Set empty slot
                itemStackImage = imageBuilder.setRegionName("clear").buildVisImage();
            }

            add(itemStackImage).expand().fill().align(Alignment.LEFT.getAlignment()).padRight(3); // Set next image

            if (itemStack != null) {
                // Setup name tag and price
                VisTable slotTable = new VisTable();
                slotTable.add(itemStack.getName()).align(Alignment.TOP_LEFT.getAlignment()).row();

                VisTable priceTable = new VisTable();
                VisTextButton button = new VisTextButton("Buy");
                priceTable.add(button);
                priceTable.add(new ImageBuilder(GameAtlas.ITEMS, 16).setRegionName("drops_44").buildVisImage());
                priceTable.add(new VisLabel(Integer.toString(price)));
                slotTable.add(priceTable).align(Alignment.BOTTOM_RIGHT.getAlignment());

                add(slotTable).expand().fill().align(Alignment.RIGHT.getAlignment());

                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // Send packet here
                        new EntityShopPacketOut(new EntityShopAction(ShopOpcodes.BUY, shopSlot)).sendPacket();
                    }
                });

                // Setup tool tips
                if (itemStackToolTip != null) {
                    itemStackToolTip.unregisterToolTip();
                    itemStackToolTip = null;
                }
                itemStackToolTip = new ItemStackToolTip(itemStack, this);
                itemStackToolTip.registerToolTip();
            }
            pack();
        }
    }
}
