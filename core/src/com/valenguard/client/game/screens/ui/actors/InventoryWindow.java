package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.valenguard.client.game.screens.ui.Buildable;
import com.valenguard.client.game.screens.ui.HideableVisWindow;
import com.valenguard.client.game.screens.ui.StageHandler;

public class InventoryWindow extends HideableVisWindow implements Buildable, Focusable {

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 5;

    private final StageHandler stageHandler;

    public InventoryWindow(StageHandler stageHandler) {
        super("Bag 1");
        this.stageHandler = stageHandler;
    }

    @Override
    public Actor build() {
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (int i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {
            InventorySlot inventorySlot = new InventorySlot(stageHandler);
            inventorySlot.build();
            add(inventorySlot);

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
}
