package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.io.type.GameAtlas;

public class ButtonBar extends VisTable implements Buildable {

    @Override
    public Actor build() {

        VisTable buttonTable = new VisTable();

        final StageHandler stageHandler = ActorUtil.getStageHandler();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);
        VisImageButton characterButton = new VisImageButton(imageBuilder.setRegionName("skill_076").buildTextureRegionDrawable(), "Character");
        VisImageButton escMenuButton = new VisImageButton(imageBuilder.setRegionName("quest_001").buildTextureRegionDrawable(), "Main Menu");
        VisImageButton inventoryButton = new VisImageButton(imageBuilder.setRegionName("quest_121").buildTextureRegionDrawable(), "Inventory");

        buttonTable.add(characterButton).padRight(10);
        buttonTable.add(escMenuButton).padRight(10);
        buttonTable.add(inventoryButton);

        add(buttonTable);

        characterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ButtonBar.class, (short) 0);
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
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ButtonBar.class, (short) 0);
                EscapeWindow escapeWindow = stageHandler.getEscapeWindow();
                if (!escapeWindow.isVisible()) {

                    // Close all open windows
                    ActorUtil.fadeOutWindow(stageHandler.getMainSettingsWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getBagWindow());
                    ActorUtil.fadeOutWindow(stageHandler.getEquipmentWindow());
                    EntityManager.getInstance().getPlayerClient().closeBankWindow();
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
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(ButtonBar.class, (short) 0);
                BagWindow bagWindow = stageHandler.getBagWindow();
                if (!bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    ActorUtil.fadeInWindow(bagWindow);
                } else if (bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    ActorUtil.fadeOutWindow(bagWindow);
                }
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), 10);
            }
        });

        pack();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), 10);
        setVisible(true);
        return this;
    }
}
