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
import com.valenguard.client.game.screens.ui.actors.game.draggable.CharacterWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.InventoryWindow;

public class ButtonBar extends VisTable implements Buildable {

    @Override
    public Actor build() {

        VisTable buttonTable = new VisTable();

        final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEM_TEXTURES, 32);
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
                CharacterWindow characterWindow = stageHandler.getCharacterWindow();
                if (!characterWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    characterWindow.fadeIn().setVisible(true);
                } else if (characterWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    characterWindow.fadeOut();
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
                    stageHandler.getInventoryWindow().setVisible(false);
                    stageHandler.getCharacterWindow().setVisible(false);
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
                InventoryWindow inventoryWindow = stageHandler.getInventoryWindow();
                if (!inventoryWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    inventoryWindow.fadeIn().setVisible(true);
                } else if (inventoryWindow.isVisible() && !stageHandler.getEscapeWindow().isVisible()) {
                    inventoryWindow.fadeOut();
                }
            }
        });

        pack();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), 10);
        setVisible(false);
        return this;
    }
}
