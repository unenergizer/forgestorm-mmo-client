package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.screens.ui.actors.Buildable;

public class ButtonBar extends VisTable implements Buildable {

    @Override
    public Actor build() {

        VisTable buttonTable = new VisTable();


        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ITEM_TEXTURES);
        TextureRegion escMenuTexture = textureAtlas.findRegion("quest_001");
        TextureRegion inventoryTexture = textureAtlas.findRegion("quest_121");

        VisImageButton escMenuButton = new VisImageButton(new TextureRegionDrawable(escMenuTexture), "Main Menu");
        VisImageButton inventoryButton = new VisImageButton(new TextureRegionDrawable(inventoryTexture), "Inventory");

        buttonTable.add(escMenuButton).padRight(10);
        buttonTable.add(inventoryButton);

        add(buttonTable);

        escMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EscapeWindow escapeWindow = Valenguard.getInstance().getStageHandler().getEscapeWindow();
                if (!escapeWindow.isVisible()) {
                    Valenguard.getInstance().getStageHandler().getInventoryWindow().setVisible(false);
                    escapeWindow.fadeIn().setVisible(true);
                } else {
                    escapeWindow.fadeOut();
                }
            }
        });

        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InventoryWindow inventoryWindow = Valenguard.getInstance().getStageHandler().getInventoryWindow();
                if (!inventoryWindow.isVisible() && !Valenguard.getInstance().getStageHandler().getEscapeWindow().isVisible()) {
                    inventoryWindow.fadeIn().setVisible(true);
                } else if (inventoryWindow.isVisible() && !Valenguard.getInstance().getStageHandler().getEscapeWindow().isVisible()) {
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
