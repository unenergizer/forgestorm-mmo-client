package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.KeyBinds;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.EscapeWindow;
import com.valenguard.client.game.screens.ui.actors.game.GameButtonBar;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.item.inventory.InventoryConstants;
import com.valenguard.client.game.world.item.inventory.InventoryType;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;

public class HotBar extends VisTable implements Buildable {

    private static final int BUTTON_PADDING = 3;
    private static final int BUTTON_TO_BUTTON_SPACE = 5;

    @Getter
    private final ItemSlotContainer itemSlotContainer = new ItemSlotContainer(this, InventoryConstants.HOT_BAR_SIZE);

    @Override
    public Actor build(final StageHandler stageHandler) {
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        final VisTable otherTable = buildOtherButtons(stageHandler);

        VisTable abilityTable = new VisTable();
        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.HOT_BAR_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(getItemSlotContainer(), InventoryType.HOT_BAR, 48, i);
            itemStackSlot.build(stageHandler);

            abilityTable.add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            getItemSlotContainer().itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.HOT_BAR_WIDTH) {
                abilityTable.row();
                columnCount = 0;
            }
        }


        add(abilityTable).padRight(1);
        add(otherTable).align(Alignment.CENTER.getAlignment());

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        final float x = (Gdx.graphics.getWidth() / 2f) - (getWidth() / 2) + (otherTable.getWidth() / 2);
//        final float y = StageHandler.WINDOW_PAD_Y;
        final float y = StageHandler.WINDOW_PAD_Y + 200;

        setPosition(x, y);
        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(x, y);
            }
        });

        pack();
//        abilityTableWidth = abilityTable.getWidth();

        pack();
        setVisible(false);
        return this;
    }

    private VisTable buildOtherButtons(final StageHandler stageHandler) {
        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 28);
        VisImageButton escMenuButton = new VisImageButton(imageBuilder.setRegionName("drops_50").buildTextureRegionDrawable(), "Main Menu (" + KeyBinds.printKey(KeyBinds.ESCAPE_ACTION) + ")");
        VisImageButton spellBookButton = new VisImageButton(imageBuilder.setRegionName("quest_165").buildTextureRegionDrawable(), "Spell Book & Abilities (" + KeyBinds.printKey(KeyBinds.SPELL_BOOK) + ")");
        VisImageButton characterButton = new VisImageButton(imageBuilder.setRegionName("skill_168").buildTextureRegionDrawable(), "Character (" + KeyBinds.printKey(KeyBinds.EQUIPMENT_WINDOW) + ")");
        VisImageButton inventoryButton = new VisImageButton(imageBuilder.setRegionName("quest_121").buildTextureRegionDrawable(), "Inventory (" + KeyBinds.printKey(KeyBinds.INVENTORY_WINDOW) + ")");

        buttonTable.add(escMenuButton).padRight(BUTTON_PADDING).padLeft(BUTTON_TO_BUTTON_SPACE);
        buttonTable.add(spellBookButton).padRight(BUTTON_PADDING);
        buttonTable.add(characterButton).padRight(BUTTON_PADDING);
        buttonTable.add(inventoryButton);

        escMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
                EscapeWindow escapeWindow = stageHandler.getEscapeWindow();
                if (!escapeWindow.isVisible()) {

                    // Close all open windows
                    ActorUtil.fadeOutWindow(stageHandler.getMainSettingsWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getBagWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getEquipmentWindow());
                    EntityManager.getInstance().getPlayerClient().closeBankWindow();
                    stageHandler.getEntityShopWindow().closeShopWindow(false);
                    ActorUtil.fadeOutWindow(stageHandler.getHelpWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getCreditsWindow());

                    ActorUtil.fadeInWindow(escapeWindow);
                } else {
                    ActorUtil.fadeOutWindow(escapeWindow);
                }
            }
        });

        characterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
                EquipmentWindow equipmentWindow = stageHandler.getEquipmentWindow();
                if (!equipmentWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    equipmentWindow.openWindow();
                } else if (equipmentWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    ActorUtil.fadeOutWindow(equipmentWindow);
                }
            }
        });


        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
                BagWindow bagWindow = stageHandler.getBagWindow();
                if (!bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    stageHandler.getBagWindow().openWindow();
                } else if (bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    stageHandler.getBagWindow().closeWindow();
                }
            }
        });

        return buttonTable;
    }
}
