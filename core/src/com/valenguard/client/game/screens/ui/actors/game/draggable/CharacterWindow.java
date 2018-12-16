package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class CharacterWindow extends HideableVisWindow implements Buildable, Focusable {


    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

//    private InventorySlot[] inventorySlots = new InventorySlot[NUM_ROWS * NUM_COLUMNS];

    public CharacterWindow() {
        super("Character");
    }

    @Override
    public Actor build() {
        DragAndDrop dragManager = Valenguard.getInstance().getStageHandler().getDragAndDrop();
        dragManager.setDragTime(0);
        addCloseButton();
        setResizable(false);

        setSize(100, 100);


//        InventorySlot inventorySlot = new InventorySlot(null);
//
//        inventorySlot.build();
//        add(inventorySlot).width(16).height(16).expand().fill();
//        dragManager.addSource(new InventorySource(inventorySlot, dragManager));
//        dragManager.addTarget(new InventoryTarget(inventorySlot, i));
//
//        inventorySlots[i] = inventorySlot;


        pack();
        centerWindow();
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
