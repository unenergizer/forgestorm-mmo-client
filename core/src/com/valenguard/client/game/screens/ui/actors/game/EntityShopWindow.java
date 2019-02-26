package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.data.EntityShopManager;
import com.valenguard.client.game.data.ItemStackManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.draggable.ItemStackToolTip;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

public class EntityShopWindow extends HideableVisWindow implements Buildable {

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);
    private final VisTextField pageDisplay = new VisTextField();
    private final VisTextButton previousPage = new VisTextButton("Previous Page");
    private final VisTextButton nextPage = new VisTextButton("Next Page");
    private final VisTextButton exit = new VisTextButton("Exit Shop");

    private EntityShopWindow entityShopWindow;
    private VisTable pageContainer = new VisTable();
    private VisTable navTable = new VisTable();

    private List<VisTable> shopPages;
    private int currentPageIndex = 0;

    @Setter
    @Getter
    private MovingEntity shopOwner;

    public EntityShopWindow() {
        super("Trade Shop");
    }

    @Override
    public Actor build() {
        entityShopWindow = this;
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(false);

        pageDisplay.setDisabled(true);
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

    private List<VisTable> buildShopPage(int shopID) {
        EntityShopManager entityShopManager = Valenguard.getInstance().getEntityShopManager();
        ItemStackManager itemStackManager = Valenguard.getInstance().getItemStackManager();

        // Generate shop slots
        List<EntityShopWindowSlot> entityShopWindowSlots = new ArrayList<EntityShopWindowSlot>();
        for (int i = 0; i < entityShopManager.getShopItemList(shopID).size(); i++) {
            ItemStack itemStack = itemStackManager.makeItemStack(entityShopManager.getItemForShop(shopID, i), 1);
            entityShopWindowSlots.add(new EntityShopWindowSlot(itemStack));
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

            shopPage.add(entityShopWindowSlot);
            columnCount++;
            pageCount++;

            // Test if we need to make a new page
            if (pageCount >= ClientConstants.BAG_SIZE) {
                // Start new page
                shopPage = new VisTable();
                shopPageList.add(shopPage);

                pageCount = 0;
                columnCount = 0;
            } else if (columnCount == ClientConstants.BAG_WIDTH) {
                // Test if we need to make a item row
                shopPage.row();
                columnCount = 0;
            }
        }

        // Generate blank spots
        int blankSpots = (shopPageList.size() * ClientConstants.BAG_SIZE) - entityShopWindowSlots.size();
        columnCount = entityShopWindowSlots.size() % ClientConstants.BAG_WIDTH;

        VisTable lastShopPage = shopPageList.get(shopPageList.size() - 1);

        for (int i = 0; i < blankSpots; i++) {
            EntityShopWindowSlot entityShopWindowSlot = new EntityShopWindowSlot(null);
            entityShopWindowSlot.setItemStackCell();
            lastShopPage.add(entityShopWindowSlot);

            columnCount++;

            if (columnCount == ClientConstants.BAG_WIDTH) {
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

    public void loadShop(int shopID) {
        resetShop();

        // Dynamic build shop pages
        shopPages = buildShopPage(shopID);
        println(getClass(), "Shop Pages: " + shopPages.size());
        changeShopPage(0);
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
         * The image that represents this cell
         */
        private VisImage itemStackImage;

        private ItemStackToolTip itemStackToolTip;

        EntityShopWindowSlot(final ItemStack itemStack) {
            this.itemStack = itemStack;
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

            add(itemStackImage); // Set next image

            if (itemStack != null) {
                if (itemStackToolTip != null) {
                    itemStackToolTip.unregisterToolTip();
                    itemStackToolTip = null;
                }
                itemStackToolTip = new ItemStackToolTip(itemStack, itemStackImage);
                itemStackToolTip.registerToolTip();
            }
            pack();
        }
    }
}
