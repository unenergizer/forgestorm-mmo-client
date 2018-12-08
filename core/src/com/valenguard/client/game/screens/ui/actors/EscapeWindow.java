package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.Buildable;
import com.valenguard.client.game.screens.ui.HideableVisWindow;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.VisabilityToggle;
import com.valenguard.client.util.Log;

public class EscapeWindow extends HideableVisWindow implements Buildable, Focusable, VisabilityToggle {

    private StageHandler stageHandler;

    public EscapeWindow(StageHandler stageHandler) {
        super("");
        this.stageHandler = stageHandler;
    }

    @Override
    public Actor build() {
        setMovable(false);
        TableUtils.setSpacingDefaults(this);
        VisTable table = new VisTable();

        final VisTextButton help = new VisTextButton("Help");
        VisTextButton credits = new VisTextButton("Credits");
        VisTextButton settings = new VisTextButton("Settings");
        VisTextButton logout = new VisTextButton("Logout");
        VisTextButton exitGame = new VisTextButton("Exit Game");
        VisTextButton returnToGame = new VisTextButton("Return to Game");
        returnToGame.setColor(Color.GREEN);

        pad(3);

        table.add(help).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(credits).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(settings).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(logout).fill().pad(0, 0, 3, 0);
        table.row();
        table.add(exitGame).fill().pad(0, 0, 6, 0);
        table.row();
        table.add(returnToGame).fill();
        table.row();

        add(table);

        pack();
        centerWindow();

        setVisible(false);

        help.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                stageHandler.getHelpWindow().fadeIn().setVisible(true);
                return true;
            }
        });

        credits.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                stageHandler.getCreditsWindow().fadeIn().setVisible(true);
                return true;
            }
        });

        settings.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                stageHandler.getMainSettingsWindow().fadeIn().setVisible(true);
                return true;
            }
        });

        logout.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.clientConnection.closeConnection();
                return true;
            }
        });

        exitGame.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });

        returnToGame.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fadeOut();
                return true;
            }
        });

        return this;
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }

    @Override
    public void show() {
        Log.println(getClass(), "show");
    }

    @Override
    public void hide() {
        Log.println(getClass(), "hide");
    }
}
