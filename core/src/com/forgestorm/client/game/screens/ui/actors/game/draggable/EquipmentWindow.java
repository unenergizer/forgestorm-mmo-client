package com.forgestorm.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.rpg.Attributes;
import com.forgestorm.client.game.rpg.SkillOpcodes;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.ExperienceUpdateListener;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.StatsUpdateListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.item.ItemStack;
import com.forgestorm.client.game.world.item.ItemStackType;
import com.forgestorm.client.io.type.GameAtlas;

import lombok.Getter;

@Getter
public class EquipmentWindow extends ItemSlotContainerWindow implements Buildable {

    private StageHandler stageHandler;
    private EquipmentPreview equipmentPreview = new EquipmentPreview();
    private ImageBuilder statIconBuilder = new ImageBuilder(GameAtlas.ITEMS, 16);

    private VisLabel levelValue = new VisLabel("0");
    private VisLabel armorValue = new VisLabel("0");
    private VisLabel damageValue = new VisLabel("0");
    private VisLabel fireValue = new VisLabel("0");
    private VisLabel iceValue = new VisLabel("0");
    private VisLabel lightningValue = new VisLabel("0");
    private VisLabel poisonValue = new VisLabel("0");

    public EquipmentWindow() {
        super("Character", ClientConstants.EQUIPMENT_INVENTORY_SIZE);
    }

    public void equipItem(ItemStack sourceItemStack, ItemStackSlot sourceSlot) {
        ItemStackSlot targetSlot;
        if (sourceItemStack.getItemStackType() == ItemStackType.RING) {
            // RING 0 -> SLOT 6
            // RING 1 -> SLOT 7

            boolean ring0Taken = getItemSlotContainer().getItemStack((byte) 8) != null;
            targetSlot = ring0Taken ? getItemSlotContainer().getItemStackSlot((byte) 7) : getItemSlotContainer().getItemStackSlot((byte) 8);
        } else {
            targetSlot = equipmentPreview.getItemStackSlot(sourceItemStack.getItemStackType());
        }

        ClientMain.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), sourceItemStack);
        boolean targetContainsItem = targetSlot.getItemStack() != null;
        new InventoryMoveActions().moveItems(sourceSlot, targetSlot, sourceItemStack, targetSlot.getItemStack());

        if (!targetContainsItem) {
            sourceSlot.setEmptyCellImage();
        }
    }

    public void unequipItem(ItemSlotContainer itemSlotContainer, ItemStack sourceItemStack, ItemStackSlot sourceSlot) {
        if (itemSlotContainer.isInventoryFull(sourceItemStack)) {
            stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL,"[RED]Cannot unequip because your bag is full!");
            return;
        }

        ItemStackSlot targetSlot = itemSlotContainer.getFreeItemStackSlot(sourceItemStack);

        new InventoryMoveActions().moveItems(sourceSlot, targetSlot, sourceItemStack, targetSlot.getItemStack());
        ClientMain.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), sourceItemStack);

        sourceSlot.setEmptyCellImage();
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        addCloseButton();
        setResizable(false);

        Actor equipmentSlotsTable = equipmentPreview.build(stageHandler, getItemSlotContainer(), getItemSlotContainer().itemStackSlots);

        /*
         Build Equipment Stats Table
          */
        VisTable statsTable = new VisTable();
        statsTable.add(addStatType("skill_alt_076", "Level", levelValue)).align(Alignment.LEFT.getAlignment()).growX().row();
        statsTable.add(addStatType("shield_10", "Armor", armorValue)).align(Alignment.LEFT.getAlignment()).growX().row();
        statsTable.add(addStatType("weapon_axe_21", "Physical Damage", damageValue)).align(Alignment.LEFT.getAlignment()).growX().row();
        statsTable.add(addStatType("skill_001", "Fire Damage", fireValue)).align(Alignment.LEFT.getAlignment()).growX().row();
        statsTable.add(addStatType("skill_alt_077", "Ice Damage", iceValue)).align(Alignment.LEFT.getAlignment()).growX().row();
        statsTable.add(addStatType("skill_015", "Lightning Damage", lightningValue)).align(Alignment.LEFT.getAlignment()).growX().row();
        statsTable.add(addStatType("skill_028", "Poison Damage", poisonValue)).align(Alignment.LEFT.getAlignment()).growX().row();

        // Display equipment and stats table on the main table
        add(equipmentSlotsTable).grow().align(Align.top).padRight(10);
        add(statsTable).expandY().align(Align.top).padRight(10);

        stopWindowClickThrough();

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

    private VisTable addStatType(String iconTexture, String statType, VisLabel valueLable) {
        final int padRight = 3;
        VisTable visTable = new VisTable();

        visTable.add(statIconBuilder.setRegionName(iconTexture).buildVisImage()).padRight(padRight);
        visTable.add(new VisLabel(statType + ":")).padRight(10).growX();
        visTable.add(valueLable);

        return visTable;
    }

    public void rebuildPreviewTable() {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        equipmentPreview.rebuildPreviewTable(playerClient);
    }

    public void openWindow() {
        getTitleLabel().setText(EntityManager.getInstance().getPlayerClient().getEntityName());
        equipmentPreview.resetFacingDirection();
        rebuildPreviewTable();
        if (!isVisible()) ActorUtil.fadeInWindow(this);
    }

    ItemStackSlot addSlotHighlight(ItemStack itemStack) {
        ItemStackSlot itemStackSlot = equipmentPreview.getItemStackSlot(itemStack.getItemStackType());
        if (itemStackSlot.getItemStack() != null) {
            itemStackSlot.highlightSlot(Color.GREEN);
        } else {
            itemStackSlot.highlightSlot(new Color(0f, 1f, 0f, .6f));
        }
        return itemStackSlot;
    }

    void removeSlotHighlight(ItemStackSlot itemStackSlot) {
        if (itemStackSlot.getItemStack() != null) {
            itemStackSlot.highlightSlot(Color.WHITE);
        } else {
            itemStackSlot.highlightSlot(itemStackSlot.getEmptySlotColor());
        }
    }
}
