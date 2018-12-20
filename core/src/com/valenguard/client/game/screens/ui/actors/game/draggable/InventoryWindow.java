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

    private ItemStackSlot[] itemStackSlots = new ItemStackSlot[NUM_ROWS * NUM_COLUMNS];

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
            ItemStackSlot itemStackSlot = new ItemStackSlot();

            itemStackSlot.build();
            add(itemStackSlot);
            dragAndDrop.addSource(new ItemStackSource(itemStackSlot, dragAndDrop));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            itemStackSlots[i] = itemStackSlot;
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
            if (itemStackSlots[i].getItemStack() != null) continue;
            itemStackSlots[i].setStack(itemStack);
            return;
        }
    }
}
