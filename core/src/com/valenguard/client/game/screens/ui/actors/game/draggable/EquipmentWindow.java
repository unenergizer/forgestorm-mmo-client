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
    private ItemStackSlot pantsSlot;

    private VisLabel armorTag = new VisLabel("Armor:");
    private VisLabel damageTag = new VisLabel("Damage:");

    private VisLabel armorValue = new VisLabel("00000");
    private VisLabel damageValue = new VisLabel("00000");

    public ItemStackSlot getItemStackSlot(ItemStackType itemStackType) {
        switch (itemStackType) {
            case HELM:
                return helmSlot;
            case CHEST:
                return chestSlot;
            case PANTS:
                return pantsSlot;
            case SHOES:
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
        weaponTable.add(pantsSlot = buildSlot(EquipmentSlotTypes.PANTS));
        equipmentSlotsTable.add(weaponTable);

        itemStackSlots[0] = helmSlot;
        itemStackSlots[1] = chestSlot;
        itemStackSlots[2] = pantsSlot;
        itemStackSlots[3] = bootsSlot;
        itemStackSlots[4] = capeSlot;
        itemStackSlots[5] = glovesSlot;
        itemStackSlots[6] = beltSlot;
        itemStackSlots[7] = ringSlot0;
        itemStackSlots[8] = ringSlot1;
        itemStackSlots[9] = necklaceSlot;
        itemStackSlots[10] = weaponSlot;
        itemStackSlots[11] = shieldSlot;
        itemStackSlots[12] = ammoSlot;

        /*
         Build Equipment Stats Table
          */
        VisTable equipmentStatsTable = new VisTable();

        VisTable tagTable = new VisTable();
        tagTable.add(armorTag).align(Align.topLeft).row();
        tagTable.add(damageTag).align(Align.topLeft).row();

        VisTable valuesTable = new VisTable();
        valuesTable.add(armorValue).align(Align.topRight).row();
        valuesTable.add(damageValue).align(Align.topRight).row();

        equipmentStatsTable.add(tagTable).expandY().align(Align.topLeft).padRight(8);
        equipmentStatsTable.add(valuesTable).expandY().align(Align.topRight);

        // Display equipment and stats table on the main table
        add(equipmentSlotsTable).grow().align(Align.top).padRight(10);
        add(equipmentStatsTable).expandY().align(Align.top);

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
                armorValue.setText(playerClientAttributes.getArmor());
                damageValue.setText(playerClientAttributes.getDamage());
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
