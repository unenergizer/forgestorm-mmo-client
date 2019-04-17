package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.StatsUpdateListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.inventory.EquipmentSlotTypes;

import lombok.Getter;

@Getter
public class EquipmentWindow extends ItemSlotContainer implements Buildable, Focusable {

    private ItemStackSlot helmSlot;
    private ItemStackSlot ammoSlot;
    private ItemStackSlot necklaceSlot;
    private ItemStackSlot capeSlot;
    private ItemStackSlot ringSlot0;
    private ItemStackSlot ringSlot1;
    private ItemStackSlot chestSlot;
    private ItemStackSlot glovesSlot;
    private ItemStackSlot beltSlot;
    private ItemStackSlot bootsSlot;
    private ItemStackSlot weaponSlot;
    private ItemStackSlot shieldSlot;
    private VisLabel armor;
    private VisLabel damage;

    public ItemStackSlot getItemStackSlot(ItemStackType itemStackType) {
        switch (itemStackType) {
            case HELM:
                return helmSlot;
            case CHEST:
                return chestSlot;
            case BOOTS:
                return bootsSlot;
            case CAPE:
                return capeSlot;
            case GLOVES:
                return glovesSlot;
            case BELT:
                return beltSlot;
            case RING:
                return ringSlot0;
            case NECKLACE:
                return necklaceSlot;
            case SWORD:
            case BOW:
                return weaponSlot;
            case SHIELD:
                return shieldSlot;
            case ARROW:
                return ammoSlot;
        }
        throw new RuntimeException("Not a valid ItemStack type for the equipment window.");
    }

    public EquipmentWindow() {
        super("Character", ClientConstants.EQUIPMENT_INVENTORY_SIZE);
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

        mainTable.add(ammoSlot = buildSlot(EquipmentSlotTypes.AMMO));
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
        weaponTable.add(weaponSlot = buildSlot(EquipmentSlotTypes.WEAPON));
        weaponTable.add(shieldSlot = buildSlot(EquipmentSlotTypes.SHIELD));
        equipmentSlotsTable.add(weaponTable);

        itemStackSlots[0] = helmSlot;
        itemStackSlots[1] = chestSlot;
        itemStackSlots[2] = bootsSlot;
        itemStackSlots[3] = capeSlot;
        itemStackSlots[4] = glovesSlot;
        itemStackSlots[5] = beltSlot;
        itemStackSlots[6] = ringSlot0;
        itemStackSlots[7] = ringSlot1;
        itemStackSlots[8] = necklaceSlot;
        itemStackSlots[9] = weaponSlot;
        itemStackSlots[10] = shieldSlot;
        itemStackSlots[11] = ammoSlot;

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

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

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
     * Builds an equipment slot for the player to equip and unequip {@link ItemStack}
     *
     * @param equipmentSlotTypes The equipment slot that defines th
     * @return A {@link ItemStackSlot} to place in this {@link EquipmentWindow}
     */
    private ItemStackSlot buildSlot(EquipmentSlotTypes equipmentSlotTypes) {
        ItemStackSlot itemStackSlot = new ItemStackSlot(this, equipmentSlotTypes.getSlotIndex(), equipmentSlotTypes.getAcceptedItemStackTypes());
        itemStackSlot.build();
        DragAndDrop dragAndDrop = ActorUtil.getStageHandler().getDragAndDrop();
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
