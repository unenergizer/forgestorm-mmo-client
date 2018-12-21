package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.ItemStackType;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class CharacterWindow extends HideableVisWindow implements Buildable, Focusable {

    private DragAndDrop dragAndDrop = Valenguard.getInstance().getStageHandler().getDragAndDrop();

    public CharacterWindow() {
        super("Character");
    }

    @Override
    public Actor build() {
        addCloseButton();
        setResizable(false);

        // top table (head)
        add(buildSlot(ItemStackType.HELM));
        this.row();

        // main table (body etc)
        VisTable mainTable = new VisTable();

        mainTable.add(buildSlot(ItemStackType.ARROW));
        mainTable.add(buildSlot(ItemStackType.NECKLACE));
        mainTable.add(buildSlot(ItemStackType.CAPE));
        mainTable.row();

        mainTable.add(buildSlot(ItemStackType.RINGS));
        mainTable.add(buildSlot(ItemStackType.CHEST));
        mainTable.add(buildSlot(ItemStackType.GLOVES));
        mainTable.row();

        mainTable.add(buildSlot(ItemStackType.RINGS));
        mainTable.add(buildSlot(ItemStackType.BELT));
        mainTable.add(buildSlot(ItemStackType.BOOTS));
        add(mainTable);
        this.row();

        // main hand/off hand
        VisTable weaponTable = new VisTable();
        weaponTable.add(buildSlot(ItemStackType.SWORD));
        weaponTable.add(buildSlot(ItemStackType.SHIELD));
        add(weaponTable);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

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

    private ItemStackSlot buildSlot(ItemStackType acceptedType) {
        ItemStackSlot itemStackSlot = new ItemStackSlot(acceptedType);
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
