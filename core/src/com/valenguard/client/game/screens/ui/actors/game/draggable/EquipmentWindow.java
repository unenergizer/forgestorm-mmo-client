package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.inventory.EquipmentSlotTypes;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.StatsUpdateListener;
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
    @Getter
    private VisLabel armor;
    @Getter
    private VisLabel damage;

    public EquipmentWindow() {
        super("Character");
    }

    @Override
    public Actor build() {
        addCloseButton();
        setResizable(false);

        /*
         Build Equipment Slots Table
          */
        VisTable equipmentSlotsTable = new VisTable();

        // top table (head)
        equipmentSlotsTable.add(helmSlot = buildSlot(EquipmentSlotTypes.HELM));
        equipmentSlotsTable.row();

        // main table (body etc)
        VisTable mainTable = new VisTable();

        mainTable.add(arrowSlot = buildSlot(EquipmentSlotTypes.AMMO));
        mainTable.add(necklaceSlot = buildSlot(EquipmentSlotTypes.NECKLACE));
        mainTable.add(capeSlot = buildSlot(EquipmentSlotTypes.CAPE));
        mainTable.row();

        mainTable.add(ringSlot0 = buildSlot(EquipmentSlotTypes.RING_0));
        mainTable.add(chestSlot = buildSlot(EquipmentSlotTypes.CHEST));
        mainTable.add(glovesSlot = buildSlot(EquipmentSlotTypes.GLOVES));
        mainTable.row();

        mainTable.add(ringSlot1 = buildSlot(EquipmentSlotTypes.RING_1));
        mainTable.add(beltSlot = buildSlot(EquipmentSlotTypes.BELT));
        mainTable.add(bootsSlot = buildSlot(EquipmentSlotTypes.BOOTS));
        equipmentSlotsTable.add(mainTable);
        equipmentSlotsTable.row();

        // main hand/off hand
        VisTable weaponTable = new VisTable();
        weaponTable.add(swordSlot = buildSlot(EquipmentSlotTypes.WEAPON));
        weaponTable.add(shieldSlot = buildSlot(EquipmentSlotTypes.SHIELD));
        equipmentSlotsTable.add(weaponTable);

        /*
         Build Equipment Stats Table
          */
        VisTable equipmentStatsTable = new VisTable();

        armor = new VisLabel("Armor: 0");
        damage = new VisLabel("Damage: 0");

        equipmentStatsTable.add(armor).align(Align.topLeft).row();
        equipmentStatsTable.add(damage).align(Align.topLeft).row();

        // Display equipment and stats table on the main table
        add(equipmentSlotsTable).padRight(4);
        add(equipmentStatsTable).align(Align.top);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        addListener(new ForceCloseWindowListener());

        addListener(new StatsUpdateListener() {
            @Override
            protected void updateStats(Attributes playerClientAttributes) {
                armor.setText("Armor: " + playerClientAttributes.getArmor());
                damage.setText("Damage: " + playerClientAttributes.getDamage());
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

    /**
     * Builds an equipment slot for the player to equip and unequip {@link com.valenguard.client.game.inventory.ItemStack}
     *
     * @param equipmentSlotTypes The equipment slot that defines th
     * @return A {@link ItemStackSlot} to place in this {@link EquipmentWindow}
     */
    private ItemStackSlot buildSlot(EquipmentSlotTypes equipmentSlotTypes) {
        ItemStackSlot itemStackSlot = new ItemStackSlot(equipmentSlotTypes.getSlotIndex(), equipmentSlotTypes.getAcceptedItemStackTypes());
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
