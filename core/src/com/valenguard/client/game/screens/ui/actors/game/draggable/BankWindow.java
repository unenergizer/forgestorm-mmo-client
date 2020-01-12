package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.inventory.InventoryConstants;
import com.valenguard.client.game.world.item.inventory.InventoryType;

public class BankWindow extends ItemSlotContainer implements Buildable, Focusable {

    public BankWindow() {
        super("Bank", InventoryConstants.BANK_SIZE);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BANK_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(this, InventoryType.BANK, i);
            itemStackSlot.build(stageHandler);

            add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.BANK_WIDTH) {
                row();
                columnCount = 0;
            }
        }

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(stageHandler.getStage().getViewport().getScreenWidth() - getWidth(), 0);
            }
        });

        pack();
        setPosition(stageHandler.getStage().getViewport().getScreenWidth() - getWidth(), 0);
        setVisible(false);
        return this;
    }

    @Override
    protected void close() {
        EntityManager.getInstance().getPlayerClient().closeBankWindow();
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }
}
