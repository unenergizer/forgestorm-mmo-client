package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.character.CharacterPreviewer;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.item.ItemStackType;
import com.forgestorm.shared.game.world.item.inventory.EquipmentSlotTypes;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisTable;

import lombok.Getter;

@Getter
public class EquipmentPreview extends VisTable {

    private final CharacterPreviewer characterPreviewer = new CharacterPreviewer(8);

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
    private int moveDirection = 0;

    public Actor build(final StageHandler stageHandler, ItemSlotContainer itemSlotContainer, ItemStackSlot[] itemStackSlots) {
        this.stageHandler = stageHandler;

        // Left side table
        VisTable leftTable = new VisTable();
        leftTable.add(helmSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.HELM)).row();
        leftTable.add(necklaceSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.NECKLACE)).row();
        leftTable.add(capeSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.CAPE)).row();
        leftTable.add(ringSlot0 = buildSlot(itemSlotContainer, EquipmentSlotTypes.RING_0)).row();
        leftTable.add(ringSlot1 = buildSlot(itemSlotContainer, EquipmentSlotTypes.RING_1)).row();

        // Character Preview Table
        previewTable = characterPreviewer.generatePreviewTable();

        // Right side table
        VisTable rightTable = new VisTable();
        rightTable.add(glovesSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.GLOVES)).row();
        rightTable.add(chestSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.CHEST)).row();
        rightTable.add(beltSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.BELT)).row();
        rightTable.add(pantsSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.PANTS)).row();
        rightTable.add(bootsSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.BOOTS)).row();

        // Put it all together
        VisTable mainTable = new VisTable();
        mainTable.add(leftTable).padRight(4);
        mainTable.add(previewTable);
        mainTable.add(rightTable).padLeft(4);

        // Bottom Table (center)
        VisTable bottomTable = new VisTable();
        bottomTable.add(weaponSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.WEAPON));
        bottomTable.add(shieldSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.SHIELD));
        bottomTable.add(ammoSlot = buildSlot(itemSlotContainer, EquipmentSlotTypes.AMMO));

        VisTable equipmentSlotsTable = new VisTable();
        equipmentSlotsTable.add(mainTable).row();
        equipmentSlotsTable.add(bottomTable).align(Alignment.CENTER.getAlignment());

        add(equipmentSlotsTable).grow().align(Align.top);

        // Assign slots
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

        pack();
        return this;
    }

    /**
     * Builds an equipment slot for the player to equip and unequip {@link ItemStack}
     *
     * @param equipmentSlotTypes The equipment slot that defines th
     * @return A {@link ItemStackSlot} to place in this {@link EquipmentPreview}
     */
    private ItemStackSlot buildSlot(ItemSlotContainer itemSlotContainer, EquipmentSlotTypes equipmentSlotTypes) {
        ItemStackSlot itemStackSlot = new ItemStackSlot(itemSlotContainer, 32, equipmentSlotTypes.getSlotIndex(), equipmentSlotTypes.getAcceptedItemStackTypes());
        itemStackSlot.build(stageHandler);
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
        dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));
        return itemStackSlot;
    }

    void rebuildPreviewTable(Player player) {
        Appearance appearance = player.getAppearance();
        characterPreviewer.generateCharacterPreview(appearance, null);
        pack();
    }

    void resetFacingDirection() {
        moveDirection = 0;
    }

    ItemStackSlot getItemStackSlot(ItemStackType itemStackType) {
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
}
