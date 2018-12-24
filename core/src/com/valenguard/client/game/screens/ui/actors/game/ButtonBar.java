package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BagWindow;

public class ButtonBar extends VisTable implements Buildable {

    @Override
    public Actor build() {

        VisTable buttonTable = new VisTable();

        final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

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
                EquipmentWindow equipmentWindow = stageHandler.getEquipmentWindow();
                if (!equipmentWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    equipmentWindow.fadeIn().setVisible(true);
                } else if (equipmentWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    equipmentWindow.fadeOut();
                }
            }
        });

        escMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EscapeWindow escapeWindow = stageHandler.getEscapeWindow();
                if (!escapeWindow.isVisible()) {

                    // Close all open windows
                    stageHandler.getMainSettingsWindow().setVisible(false);
                    stageHandler.getBagWindow().setVisible(false);
                    stageHandler.getEquipmentWindow().setVisible(false);
                    stageHandler.getHelpWindow().setVisible(false);
                    stageHandler.getCreditsWindow().setVisible(false);

                    escapeWindow.fadeIn().setVisible(true);
                } else {
                    escapeWindow.fadeOut();
                }
            }
        });

        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BagWindow bagWindow = stageHandler.getBagWindow();
                if (!bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    bagWindow.fadeIn().setVisible(true);
                } else if (bagWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    bagWindow.fadeOut();
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
        setVisible(false);
        return this;
    }
}
