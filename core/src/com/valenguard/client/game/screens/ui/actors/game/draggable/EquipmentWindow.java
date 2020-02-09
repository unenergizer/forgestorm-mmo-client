package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.rpg.SkillOpcodes;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.character.CharacterPreviewer;
import com.valenguard.client.game.screens.ui.actors.event.ExperienceUpdateListener;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.StatsUpdateListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.game.world.item.inventory.EquipmentSlotTypes;
import com.valenguard.client.game.world.maps.MoveDirection;

import lombok.Getter;

@Getter
public class EquipmentWindow extends ItemSlotContainer implements Buildable, Focusable {

    private final CharacterPreviewer characterPreviewer = new CharacterPreviewer();

    private StageHandler stageHandler;

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

    private VisTable previewTable;

    private VisLabel levelTag = new VisLabel("Level: ");
    private VisLabel armorTag = new VisLabel("Armor:");
    private VisLabel damageTag = new VisLabel("Damage:");

    private VisLabel levelValue = new VisLabel("0000");
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
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        addCloseButton();
        setResizable(false);

        ///////////////////////////////

        // Left side table
        VisTable leftTable = new VisTable();
        leftTable.add(helmSlot = buildSlot(EquipmentSlotTypes.HELM)).row();
        leftTable.add(necklaceSlot = buildSlot(EquipmentSlotTypes.NECKLACE)).row();
        leftTable.add(capeSlot = buildSlot(EquipmentSlotTypes.CAPE)).row();
        leftTable.add(ringSlot0 = buildSlot(EquipmentSlotTypes.RING_0)).row();
        leftTable.add(ringSlot1 = buildSlot(EquipmentSlotTypes.RING_1)).row();

        // Character Preview Table
        previewTable = characterPreviewer.fillPreviewTable(characterPreviewer.generateBasicAppearance(), MoveDirection.SOUTH, 8);

        // Right side table
        VisTable rightTable = new VisTable();
        rightTable.add(glovesSlot = buildSlot(EquipmentSlotTypes.GLOVES)).row();
        rightTable.add(chestSlot = buildSlot(EquipmentSlotTypes.CHEST)).row();
        rightTable.add(beltSlot = buildSlot(EquipmentSlotTypes.BELT)).row();
        rightTable.add(pantsSlot = buildSlot(EquipmentSlotTypes.PANTS)).row();
        rightTable.add(bootsSlot = buildSlot(EquipmentSlotTypes.BOOTS)).row();

        // Put it all together
        VisTable mainTable = new VisTable();
        mainTable.add(leftTable).padRight(4);
        mainTable.add(previewTable);
        mainTable.add(rightTable).padLeft(4);

        // Bottom Table (center)
        VisTable bottomTable = new VisTable();
        bottomTable.add(weaponSlot = buildSlot(EquipmentSlotTypes.WEAPON));
        bottomTable.add(shieldSlot = buildSlot(EquipmentSlotTypes.SHIELD));
        bottomTable.add(ammoSlot = buildSlot(EquipmentSlotTypes.AMMO));


        VisTable equipmentSlotsTable = new VisTable();
        equipmentSlotsTable.add(mainTable).row();
        equipmentSlotsTable.add(bottomTable).align(Alignment.CENTER.getAlignment());

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
        tagTable.add(levelTag).align(Align.topLeft).row();
        tagTable.add(armorTag).align(Align.topLeft).row();
        tagTable.add(damageTag).align(Align.topLeft).row();

        VisTable valuesTable = new VisTable();
        valuesTable.add(levelValue).align(Align.topRight).row();
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

        addListener(new ExperienceUpdateListener() {
            @Override
            protected void updateLevel(SkillOpcodes skillOpcode, int level) {
                if (skillOpcode == SkillOpcodes.MELEE) levelValue.setText(level);
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
        itemStackSlot.build(stageHandler);
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
        dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));
        return itemStackSlot;
    }

    public void rebuildPreviewTable() {
        previewTable.clearChildren();
        Appearance appearance = EntityManager.getInstance().getPlayerClient().getAppearance();
        VisTable visImageTable = characterPreviewer.fillPreviewTable(appearance, MoveDirection.SOUTH, 15);
        previewTable.add(visImageTable).row();
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }
}
