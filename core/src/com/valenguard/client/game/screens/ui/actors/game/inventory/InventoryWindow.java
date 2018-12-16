package com.valenguard.client.game.screens.ui.actors.game.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class InventoryWindow extends HideableVisWindow implements Buildable, Focusable {

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 5;

    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

    private DragAndDrop dragManager = new DragAndDrop();

    private InventorySlot[] inventorySlots = new InventorySlot[NUM_ROWS * NUM_COLUMNS];

    public InventoryWindow() {
        super("Bag 1");
    }

    @Override
    public Actor build() {
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {
            InventorySlot inventorySlot = new InventorySlot(null);

            inventorySlot.build();
            add(inventorySlot).width(16).height(16).expand().fill();
            dragManager.addSource(new InventorySource(inventorySlot, dragManager));
            dragManager.addTarget(new InventoryTarget(inventorySlot, i));

            inventorySlots[i] = inventorySlot;
            columnCount++;

            if (columnCount == NUM_COLUMNS) {
                row();
                columnCount = 0;
            }
        }

        pack();
        setPosition(stageHandler.getStage().getViewport().getScreenWidth() - this.getWidth(), 0);
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
