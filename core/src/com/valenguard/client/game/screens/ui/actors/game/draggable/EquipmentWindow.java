package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.EquipmentSlots;
import com.valenguard.client.game.inventory.InventoryType;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

import lombok.Getter;

public class EquipmentWindow extends HideableVisWindow implements Buildable, Focusable {

    private final DragAndDrop dragAndDrop = Valenguard.getInstance().getStageHandler().getDragAndDrop();

    @Getter
    private ItemStackSlot helmSlot;
    @Getter
    private ItemStackSlot arrowSlot;
    @Getter
    private ItemStackSlot necklaceSlot;
    @Getter
    private ItemStackSlot capeSlot;
    @Getter
    private ItemStackSlot ringSlot0;
    @Getter
    private ItemStackSlot ringSlot1;
    @Getter
    private ItemStackSlot chestSlot;
    @Getter
    private ItemStackSlot glovesSlot;
    @Getter
    private ItemStackSlot beltSlot;
    @Getter
    private ItemStackSlot bootsSlot;
    @Getter
    private ItemStackSlot swordSlot;
    @Getter
    private ItemStackSlot shieldSlot;

    public EquipmentWindow() {
        super("Character");
    }

    @Override
    public Actor build() {
        addCloseButton();
        setResizable(false);

        // top table (head)
        add(helmSlot = buildSlot(EquipmentSlots.HELM));
        this.row();

        // main table (body etc)
        VisTable mainTable = new VisTable();

        mainTable.add(arrowSlot = buildSlot(EquipmentSlots.AMMO));
        mainTable.add(necklaceSlot = buildSlot(EquipmentSlots.NECKLACE));
        mainTable.add(capeSlot = buildSlot(EquipmentSlots.CAPE));
        mainTable.row();

        mainTable.add(ringSlot0 = buildSlot(EquipmentSlots.RING_0));
        mainTable.add(chestSlot = buildSlot(EquipmentSlots.CHEST));
        mainTable.add(glovesSlot = buildSlot(EquipmentSlots.GLOVES));
        mainTable.row();

        mainTable.add(ringSlot1 = buildSlot(EquipmentSlots.RING_1));
        mainTable.add(beltSlot = buildSlot(EquipmentSlots.BELT));
        mainTable.add(bootsSlot = buildSlot(EquipmentSlots.BOOTS));
        add(mainTable);
        this.row();

        // main hand/off hand
        VisTable weaponTable = new VisTable();
        weaponTable.add(swordSlot = buildSlot(EquipmentSlots.WEAPON));
        weaponTable.add(shieldSlot = buildSlot(EquipmentSlots.SHIELD));
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

    /**
     * Builds an equipment slot for the player to equip and unequip {@link com.valenguard.client.game.inventory.ItemStack}
     *
     * @param equipmentSlots The equipment slot that defines th
     * @return A {@link ItemStackSlot} to place in this {@link EquipmentWindow}
     */
    private ItemStackSlot buildSlot(EquipmentSlots equipmentSlots) {
        ItemStackSlot itemStackSlot = new ItemStackSlot(InventoryType.EQUIPMENT, equipmentSlots.getSlotIndex(), equipmentSlots.getAcceptedItemStackTypes());
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
