package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.GameButtonBar;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.inventory.InventoryConstants;
import com.valenguard.client.game.world.item.inventory.InventoryType;

public class BankWindow extends ItemSlotContainer implements Buildable {

    private StageHandler stageHandler;

    public BankWindow() {
        super("Bank", InventoryConstants.BANK_SIZE);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        addCloseButton();
        setResizable(false);

        VisTable slotTable = new VisTable();

        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BANK_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(this, InventoryType.BANK, i);
            itemStackSlot.build(stageHandler);

            slotTable.add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.BANK_WIDTH) {
                slotTable.row();
                columnCount = 0;
            }
        }

        VisTextButton depositBagItems = new VisTextButton("Deposit Bag Items");
        VisTextButton depositWornItems = new VisTextButton("Deposit Worn Items");

        VisTable buttonTable = new VisTable();
        buttonTable.add(depositBagItems).align(Alignment.RIGHT.getAlignment());
        buttonTable.add(depositWornItems).align(Alignment.RIGHT.getAlignment());

        depositBagItems.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                depositItems(stageHandler.getBagWindow());
            }
        });

        depositWornItems.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                depositItems(stageHandler.getEquipmentWindow());
            }
        });

        add(slotTable).row();
        add(buttonTable).align(Alignment.RIGHT.getAlignment());

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findWindowPosition(false);
            }
        });

        pack();
        findWindowPosition(false);
        setVisible(false);
        return this;
    }

    private void depositItems(ItemSlotContainer itemSlotContainer) {
        for (ItemStackSlot itemStackSlot : itemSlotContainer.itemStackSlots) {
            if (itemStackSlot.getItemStack() == null) continue;

            ItemStackSlot targetItemStackSlot = getFreeItemStackSlot(itemStackSlot.getItemStack());

            if (targetItemStackSlot != null) {
                ItemStack targetItemStack = targetItemStackSlot.getItemStack();
                new InventoryMoveActions().moveItems(itemStackSlot, targetItemStackSlot, itemStackSlot.getItemStack(), targetItemStack);
            } else {
                new InventoryMoveActions().moveItems(itemStackSlot, null, itemStackSlot.getItemStack(), null);
            }
        }
    }

    public void openWindow() {
        BagWindow bagWindow = stageHandler.getBagWindow();
        if (!bagWindow.isVisible()) ActorUtil.fadeInWindow(bagWindow);
        ActorUtil.fadeInWindow(this);
        findWindowPosition(false);
    }

    void findWindowPosition(boolean ignoreBagVisible) {
        BagWindow bagWindow = stageHandler.getBagWindow();
        float bagWindowY = bagWindow.getY() + bagWindow.getHeight();
        float bankWindowX = stageHandler.getStage().getViewport().getScreenWidth() - getWidth() - StageHandler.WINDOW_PAD_X;

        if (bagWindow.isVisible() && !ignoreBagVisible) {
            setPosition(bankWindowX, bagWindowY + StageHandler.WINDOW_PAD_Y);
        } else {

            GameButtonBar gameButtonBar = stageHandler.getGameButtonBar();
            float endPosition = gameButtonBar.getX() + gameButtonBar.getWidth() + gameButtonBar.getPadLeft();

            if (endPosition > bankWindowX) {
                setPosition(bankWindowX, gameButtonBar.getHeight() + gameButtonBar.getY());
            } else {
                setPosition(bankWindowX, StageHandler.WINDOW_PAD_Y);
            }
        }
    }

    @Override
    protected void close() {
        EntityManager.getInstance().getPlayerClient().closeBankWindow();
    }

    void swapInventories(ItemStack sourceItemStack, ItemStackSlot sourceSlot, ItemSlotContainer itemSlotContainer) {
        if (!stageHandler.getBankWindow().isVisible()) {
            stageHandler.getChatWindow().appendChatMessage("[RED]Cannot transfer item because the bank is not open!");
            return;
        }

        if (itemSlotContainer.isInventoryFull(sourceItemStack)) {
            stageHandler.getChatWindow().appendChatMessage("[RED]Cannot transfer item because the inventory is full!");
            return;
        }

        ItemStackSlot targetSlot = itemSlotContainer.getFreeItemStackSlot(sourceItemStack);

        new InventoryMoveActions().moveItems(sourceSlot, targetSlot, sourceItemStack, targetSlot.getItemStack());
        Valenguard.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), sourceItemStack);

        sourceSlot.setEmptyCellImage();
    }
}
