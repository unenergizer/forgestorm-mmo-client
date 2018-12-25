package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.InventoryType;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class EquipmentWindow extends HideableVisWindow implements Buildable, Focusable {

    private DragAndDrop dragAndDrop = Valenguard.getInstance().getStageHandler().getDragAndDrop();

    public EquipmentWindow() {
        super("Character");
    }

    @Override
    public Actor build() {
        addCloseButton();
        setResizable(false);

        // top table (head)
        add(buildSlot(ItemStackType.HELM, (byte) 0));
        this.row();

        // main table (body etc)
        VisTable mainTable = new VisTable();

        mainTable.add(buildSlot(ItemStackType.ARROW, (byte) 1));
        mainTable.add(buildSlot(ItemStackType.NECKLACE, (byte) 2));
        mainTable.add(buildSlot(ItemStackType.CAPE, (byte) 3));
        mainTable.row();

        mainTable.add(buildSlot(ItemStackType.RING, (byte) 4));
        mainTable.add(buildSlot(ItemStackType.CHEST, (byte) 5));
        mainTable.add(buildSlot(ItemStackType.GLOVES, (byte) 6));
        mainTable.row();

        mainTable.add(buildSlot(ItemStackType.RING, (byte) 7));
        mainTable.add(buildSlot(ItemStackType.BELT, (byte) 8));
        mainTable.add(buildSlot(ItemStackType.BOOTS, (byte) 9));
        add(mainTable);
        this.row();

        // main hand/off hand
        VisTable weaponTable = new VisTable();
        weaponTable.add(buildSlot(ItemStackType.SWORD, (byte) 10));
        weaponTable.add(buildSlot(ItemStackType.SHIELD, (byte) 11));
        add(weaponTable);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        addListener(new ForceCloseWindowListener());

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        pack();
        centerWindow();
        setVisible(false);
        return this;
    }

    private ItemStackSlot buildSlot(ItemStackType acceptedType, byte inventoryIndex) {
        ItemStackSlot itemStackSlot = new ItemStackSlot(InventoryType.EQUIPMENT, inventoryIndex, acceptedType);
        itemStackSlot.build();
        dragAndDrop.addSource(new ItemStackSource(itemStackSlot, dragAndDrop));
        dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));
        return itemStackSlot;
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }
}
