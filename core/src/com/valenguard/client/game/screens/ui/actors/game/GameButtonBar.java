package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.abilities.AbilityManager;
import com.valenguard.client.game.audio.AudioManager;
import com.valenguard.client.game.input.KeyBinds;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;

public class GameButtonBar extends VisTable implements Buildable {

    private static final int BUTTON_PADDING = 3;
    private static final int BUTTON_TO_BUTTON_SPACE = 5;

    private final AbilityManager abilityManager = Valenguard.getInstance().getAbilityManager();
    private final AudioManager audioManager = Valenguard.getInstance().getAudioManager();

    private final GameButtonBar gameButtonBar;

    @Getter
    private VisImageButton action1;
    @Getter
    private VisImageButton action2;
    @Getter
    private VisImageButton action3;
    @Getter
    private VisImageButton action4;
    @Getter
    private VisImageButton action5;
    @Getter
    private VisImageButton action6;

    @Getter
    private float abilityTableWidth;

    public GameButtonBar() {
        this.gameButtonBar = this;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {

        VisTable abilityTable = buildAbilityButtons();
        final VisTable otherTable = buildOtherButtons(stageHandler);


        add(abilityTable).padRight(1);
        add(otherTable).align(Alignment.CENTER.getAlignment());

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2) + (otherTable.getWidth() / 2), 10);
            }
        });

        pack();
        abilityTableWidth = abilityTable.getWidth();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2) + (otherTable.getWidth() / 2), 10);
        setVisible(false);
        return this;
    }

    public void setCoolingDown(VisImageButton button) {
        button.setDisabled(true);
        button.setColor(Color.RED);
    }

    public void resetButton(VisImageButton button) {
        button.setDisabled(false);
        button.setColor(Color.WHITE);
    }

    public void canUseAbilities(boolean canUse) {
        if (canUse) {
            action1.setDisabled(false);
            action2.setDisabled(false);
            action3.setDisabled(false);
            action4.setDisabled(false);
            action5.setDisabled(false);
            action6.setDisabled(false);
        } else {
            action1.setDisabled(true);
            action2.setDisabled(true);
            action3.setDisabled(true);
            action4.setDisabled(true);
            action5.setDisabled(true);
            action6.setDisabled(true);
        }
    }

    private VisTable buildAbilityButtons() {
        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 48);
        action1 = new VisImageButton(imageBuilder.setRegionName("skill_061").buildTextureRegionDrawable(), "Spell 1 (" + KeyBinds.printKey(KeyBinds.ACTION_1) + ")");
        action2 = new VisImageButton(imageBuilder.setRegionName("weapon_arrow_06").buildTextureRegionDrawable(), "Spell 2 (" + KeyBinds.printKey(KeyBinds.ACTION_2) + ")");
        action3 = new VisImageButton(imageBuilder.setRegionName("clear_cell").buildTextureRegionDrawable(), "Spell 3 (" + KeyBinds.printKey(KeyBinds.ACTION_3) + ")");
        action4 = new VisImageButton(imageBuilder.setRegionName("clear_cell").buildTextureRegionDrawable(), "Spell 3 (" + KeyBinds.printKey(KeyBinds.ACTION_3) + ")");
        action5 = new VisImageButton(imageBuilder.setRegionName("clear_cell").buildTextureRegionDrawable(), "Spell 3 (" + KeyBinds.printKey(KeyBinds.ACTION_3) + ")");
        action6 = new VisImageButton(imageBuilder.setRegionName("clear_cell").buildTextureRegionDrawable(), "Spell 3 (" + KeyBinds.printKey(KeyBinds.ACTION_3) + ")");

        action3.setDisabled(true);
        action4.setDisabled(true);
        action5.setDisabled(true);
        action6.setDisabled(true);

        buttonTable.add(action1).padRight(BUTTON_PADDING);
        buttonTable.add(action2).padRight(BUTTON_PADDING);
        buttonTable.add(action3).padRight(BUTTON_PADDING);
        buttonTable.add(action4).padRight(BUTTON_PADDING);
        buttonTable.add(action5).padRight(BUTTON_PADDING);
        buttonTable.add(action6).padRight(BUTTON_PADDING);

        action1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 0, gameButtonBar, action1);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        action2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 1, gameButtonBar, action2);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        action3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 2, gameButtonBar, action3);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        action4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 2, gameButtonBar, action4);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        action5.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 2, gameButtonBar, action5);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        action6.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 2, gameButtonBar, action6);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        return buttonTable;
    }

    private VisTable buildOtherButtons(final StageHandler stageHandler) {
        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 28);
        VisImageButton escMenuButton = new VisImageButton(imageBuilder.setRegionName("quest_179").buildTextureRegionDrawable(), "Main Menu (" + KeyBinds.printKey(KeyBinds.ESCAPE_ACTION) + ")");
        VisImageButton characterButton = new VisImageButton(imageBuilder.setRegionName("skill_168").buildTextureRegionDrawable(), "Character (" + KeyBinds.printKey(KeyBinds.EQUIPMENT_WINDOW) + ")");
        VisImageButton inventoryButton = new VisImageButton(imageBuilder.setRegionName("quest_121").buildTextureRegionDrawable(), "Inventory (" + KeyBinds.printKey(KeyBinds.INVENTORY_WINDOW) + ")");

        buttonTable.add(escMenuButton).padRight(BUTTON_PADDING).padLeft(BUTTON_TO_BUTTON_SPACE);
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
                    ActorUtil.fadeInWindow(equipmentWindow);
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
                    ActorUtil.fadeInWindow(bagWindow);
                } else if (bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    ActorUtil.fadeOutWindow(bagWindow);
                }
            }
        });

        return buttonTable;
    }
}
