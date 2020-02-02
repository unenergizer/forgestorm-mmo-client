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

    private final AbilityManager abilityManager = Valenguard.getInstance().getAbilityManager();
    private final AudioManager audioManager = Valenguard.getInstance().getAudioManager();

    private final GameButtonBar gameButtonBar;

    @Getter
    private VisImageButton actionOne;
    @Getter
    private VisImageButton actionTwo;
    @Getter
    private VisImageButton actionThree;

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

    public void canUseAbilities(boolean bool) {
        if (bool) {
            actionOne.setDisabled(true);
            actionTwo.setDisabled(true);
            actionThree.setDisabled(true);
        } else {
            actionOne.setDisabled(false);
            actionTwo.setDisabled(false);
            actionThree.setDisabled(false);
        }
    }

    private VisTable buildAbilityButtons() {
        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 48);
        actionOne = new VisImageButton(imageBuilder.setRegionName("skill_061").buildTextureRegionDrawable(), "Spell 1 (" + KeyBinds.printKey(KeyBinds.ACTION_1) + ")");
        actionTwo = new VisImageButton(imageBuilder.setRegionName("weapon_arrow_06").buildTextureRegionDrawable(), "Spell 2 (" + KeyBinds.printKey(KeyBinds.ACTION_2) + ")");
        actionThree = new VisImageButton(imageBuilder.setRegionName("quest_004").buildTextureRegionDrawable(), "Spell 3 (" + KeyBinds.printKey(KeyBinds.ACTION_3) + ")");

        actionThree.setColor(Color.RED);
        actionThree.setDisabled(true);

        buttonTable.add(actionOne).padRight(1);
        buttonTable.add(actionTwo).padRight(1);
        buttonTable.add(actionThree);

        actionOne.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 0, gameButtonBar, actionOne);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        actionTwo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 1, gameButtonBar, actionTwo);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        actionThree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 2, gameButtonBar, actionThree);
                audioManager.getSoundManager().playSoundFx(GameButtonBar.class, (short) 0);
            }
        });

        return buttonTable;
    }

    private VisTable buildOtherButtons(final StageHandler stageHandler) {
        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 28);
        VisImageButton characterButton = new VisImageButton(imageBuilder.setRegionName("skill_076").buildTextureRegionDrawable(), "Character (" + KeyBinds.printKey(KeyBinds.EQUIPMENT_WINDOW) + ")");
        VisImageButton escMenuButton = new VisImageButton(imageBuilder.setRegionName("quest_001").buildTextureRegionDrawable(), "Main Menu (" + KeyBinds.printKey(KeyBinds.ESCAPE_ACTION) + ")");
        VisImageButton inventoryButton = new VisImageButton(imageBuilder.setRegionName("quest_121").buildTextureRegionDrawable(), "Inventory (" + KeyBinds.printKey(KeyBinds.INVENTORY_WINDOW) + ")");

        buttonTable.add(characterButton).padRight(1);
        buttonTable.add(escMenuButton).padRight(1);
        buttonTable.add(inventoryButton);

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
