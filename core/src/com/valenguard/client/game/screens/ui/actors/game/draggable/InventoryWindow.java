package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class InventoryWindow extends HideableVisWindow implements Buildable, Focusable {

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 5;

    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

    private InventorySlot[] inventorySlots = new InventorySlot[NUM_ROWS * NUM_COLUMNS];

    public InventoryWindow() {
        super("Inventory");
    }

    @Override
    public Actor build() {
        DragAndDrop dragAndDrop = Valenguard.getInstance().getStageHandler().getDragAndDrop();
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {
            InventorySlot inventorySlot = new InventorySlot(null);

            inventorySlot.build();
            add(inventorySlot);
            dragAndDrop.addSource(new InventorySource(inventorySlot, dragAndDrop));
            dragAndDrop.addTarget(new InventoryTarget(inventorySlot, i));

            inventorySlots[i] = inventorySlot;
            columnCount++;

            if (columnCount == NUM_COLUMNS) {
                row();
                columnCount = 0;
            }
        }

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
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }

    public void addItemStack(ItemStack itemStack) {
        for (byte i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {
            if (inventorySlots[i].getItemStack() != null) continue;
            inventorySlots[i].setStack(itemStack);
            return;
        }
    }
}
