package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.rpg.SkillOpcodes;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ExperienceUpdateListener;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.StatsUpdateListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackType;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;

@Getter
public class EquipmentWindow extends ItemSlotContainer implements Buildable {

    private StageHandler stageHandler;
    private EquipmentPreview equipmentPreview = new EquipmentPreview();

    private VisLabel levelTag = new VisLabel("Level: ");
    private VisLabel armorTag = new VisLabel("Armor:");
    private VisLabel damageTag = new VisLabel("Damage:");

    private VisLabel levelValue = new VisLabel("0000");
    private VisLabel armorValue = new VisLabel("00000");
    private VisLabel damageValue = new VisLabel("00000");

    public EquipmentWindow() {
        super("Character", ClientConstants.EQUIPMENT_INVENTORY_SIZE);
    }

    public void equipItem(ItemStack sourceItemStack, ItemStackSlot sourceSlot) {
        ItemStackSlot targetSlot;
        if (sourceItemStack.getItemStackType() == ItemStackType.RING) {
            // RING 0 -> SLOT 6
            // RING 1 -> SLOT 7

            boolean ring0Taken = getItemStack((byte) 8) != null;
            targetSlot = ring0Taken ? getItemStackSlot((byte) 7) : getItemStackSlot((byte) 8);
        } else {
            targetSlot = equipmentPreview.getItemStackSlot(sourceItemStack.getItemStackType());
        }

        Valenguard.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), sourceItemStack);
        boolean targetContainsItem = targetSlot.getItemStack() != null;
        new InventoryMoveActions().moveItems(sourceSlot, targetSlot, sourceItemStack, targetSlot.getItemStack());

        if (!targetContainsItem) {
            sourceSlot.setEmptyCellImage();
        }
    }

    public void unequipItem(ItemStack sourceItemStack, ItemStackSlot sourceSlot) {
        BagWindow bagWindow = stageHandler.getBagWindow();
        if (bagWindow.isInventoryFull(sourceItemStack)) {
            stageHandler.getChatWindow().appendChatMessage("[RED]Cannot unequip because your bag is full!");
            return;
        }

        ItemStackSlot targetSlot = bagWindow.getFreeItemStackSlot(sourceItemStack);

        new InventoryMoveActions().moveItems(sourceSlot, targetSlot, sourceItemStack, targetSlot.getItemStack());
        Valenguard.getInstance().getAudioManager().getSoundManager().playItemStackSoundFX(getClass(), sourceItemStack);

        sourceSlot.setEmptyCellImage();
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        addCloseButton();
        setResizable(false);

        Actor equipmentSlotsTable = equipmentPreview.build(stageHandler, this, itemStackSlots);

        /*
         Build Equipment Stats Table
          */
        VisTable equipmentStatsTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 16);
        final int padRight = 3;
        VisTable tagTable = new VisTable();
        tagTable.add(imageBuilder.setRegionName("skill_alt_076").buildVisImage()).padRight(padRight);
        tagTable.add(levelTag).align(Align.topLeft).row();
        tagTable.add(imageBuilder.setRegionName("shield_10").buildVisImage()).padRight(padRight);
        tagTable.add(armorTag).align(Align.topLeft).row();
        tagTable.add(imageBuilder.setRegionName("weapon_axe_21").buildVisImage()).padRight(padRight);
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
