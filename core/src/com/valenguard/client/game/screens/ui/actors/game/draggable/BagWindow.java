package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.InventoryType;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class BagWindow extends HideableVisWindow implements Buildable, Focusable {

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 5;

    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

    private final ItemStackSlot[] itemStackSlots = new ItemStackSlot[NUM_ROWS * NUM_COLUMNS];

    public BagWindow() {
        super("Inventory");
    }

    @Override
    public Actor build() {
        DragAndDrop dragAndDrop = Valenguard.getInstance().getStageHandler().getDragAndDrop();
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(InventoryType.BAG_1, i);
            itemStackSlot.build();

            add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(itemStackSlot, dragAndDrop));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == NUM_COLUMNS) {
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
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }

    /**
     * Adds an {@link ItemStack} to an empty bag slot.
     *
     * @param itemStack The {@link ItemStack} to add to the players inventory.
     */
    public void addItemStack(ItemStack itemStack) {
        for (byte i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {

            // Find empty slot
            if (itemStackSlots[i].getItemStack() != null) continue;

            // Empty slot found. Placing item
            itemStackSlots[i].setItemStack(itemStack);
            return;
        }
    }

    public void removeItemStack(byte slotIndex) {
        itemStackSlots[slotIndex].setEmptyCellImage();
        itemStackSlots[slotIndex].deleteStack();
    }
}
