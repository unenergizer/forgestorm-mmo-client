package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
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

import lombok.Getter;
import lombok.Setter;

public class EntityShopWindow extends HideableVisWindow implements Buildable {

    private final ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);

    private EntityShopWindow entityShopWindow;
    private VisTable itemCellTable = new VisTable();


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

        buildShopPage(2);

        add(itemCellTable).row();

        TextButton exit = new TextButton("Exit Shop", VisUI.getSkin());
        add(exit).expand().fill();

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

    private void buildShopPage(int shopID) {
        EntityShopManager entityShopManager = Valenguard.getInstance().getEntityShopManager();
        ItemStackManager itemStackManager = Valenguard.getInstance().getItemStackManager();

        int columnCount = 0;
        for (int i = 0; i < ClientConstants.BAG_SIZE; i++) {

            Integer itemStackID = entityShopManager.getItemForShop(shopID, i);
            ItemStack itemStack;

            if (itemStackID != null) {
                itemStack = itemStackManager.makeItemStack(itemStackID, 1);
            } else {
                itemStack = null;
            }

            EntityShopWindowSlot entityShopWindowSlot = new EntityShopWindowSlot(i, itemStack);
            entityShopWindowSlot.setItemStackCell();

            itemCellTable.add(entityShopWindowSlot);
            columnCount++;

            if (columnCount == ClientConstants.BAG_WIDTH) {
                itemCellTable.row();
                columnCount = 0;
            }
        }
    }

    /**
     * This class holds information for a particular {@link EntityShopWindowSlot}
     * within a {@link EntityShopWindow}
     */
    private class EntityShopWindowSlot extends VisTable {

        private final int index;

        /**
         * The {@link ItemStack} that is being sold by the vendor
         */
        private final ItemStack itemStack;

        private EntityShopWindowSlot entityShopWindowSlot;

        /**
         * The image that represents this cell
         */
        private VisImage itemStackImage;

        private ItemStackToolTip itemStackToolTip;

        EntityShopWindowSlot(final int index, final ItemStack itemStack) {
            this.index = index;
            this.itemStack = itemStack;
            this.entityShopWindowSlot = this;
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
