package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.GameButtonBar;
import com.valenguard.client.game.world.item.inventory.InventoryConstants;
import com.valenguard.client.game.world.item.inventory.InventoryType;

public class BagWindow extends ItemSlotContainer implements Buildable, Focusable {

    private StageHandler stageHandler;

    public BagWindow() {
        super("Inventory", InventoryConstants.BAG_SIZE);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BAG_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(this, InventoryType.BAG_1, i);
            itemStackSlot.build(stageHandler);

            add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.BAG_WIDTH) {
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
                findPosition();
            }
        });

        pack();
        findPosition();
        setVisible(false);
        return this;
    }

    private void findPosition() {
        GameButtonBar gameButtonBar = stageHandler.getGameButtonBar();
        float endPosition = gameButtonBar.getX() + gameButtonBar.getWidth() + gameButtonBar.getPadLeft();
        float bagX = stageHandler.getStage().getViewport().getScreenWidth() - getWidth() - StageHandler.WINDOW_PAD_X;

        if (endPosition > bagX) {
            setPosition(bagX, gameButtonBar.getHeight() + gameButtonBar.getY());
        } else {
            setPosition(bagX, StageHandler.WINDOW_PAD_Y);
        }
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }
}
