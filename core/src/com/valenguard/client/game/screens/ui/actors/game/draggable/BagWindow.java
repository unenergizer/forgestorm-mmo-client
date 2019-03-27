package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.item.inventory.InventoryConstants;
import com.valenguard.client.game.world.item.inventory.InventoryType;

public class BagWindow extends ItemSlotContainer implements Buildable, Focusable {

    public BagWindow() {
        super("Inventory", InventoryConstants.BAG_SIZE);
    }

    @Override
    public Actor build() {
        DragAndDrop dragAndDrop = ActorUtil.getStageHandler().getDragAndDrop();
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BAG_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(InventoryType.BAG_1, i);
            itemStackSlot.build();

            add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(itemStackSlot, dragAndDrop));
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
                setPosition(ActorUtil.getStage().getViewport().getScreenWidth() - getWidth(), 0);
            }
        });

        pack();
        setPosition(ActorUtil.getStage().getViewport().getScreenWidth() - getWidth(), 0);
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
