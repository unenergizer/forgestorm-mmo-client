package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.Buildable;

public class ButtonBar extends VisTable implements Buildable {

    @Override
    public Actor build() {

        VisTable buttonTable = new VisTable();
        VisTextButton settingsButton = new VisTextButton("E");
        VisTextButton inventoryButton = new VisTextButton("I");

        buttonTable.add(settingsButton).width(30).height(30).padRight(10);
        buttonTable.add(inventoryButton).width(30).height(30);

        add(buttonTable);

        settingsButton.addListener(new ChangeListener() {
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
