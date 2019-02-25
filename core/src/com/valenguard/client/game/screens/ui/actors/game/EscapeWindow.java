package com.valenguard.client.game.screens.ui.actors.game;

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
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class EscapeWindow extends HideableVisWindow implements Buildable, Focusable {

    private EscapeWindow escapeWindow;

    public EscapeWindow() {
        super("");
    }

    @Override
    public Actor build() {
        escapeWindow = this;
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

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        help.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ActorUtil.fadeOutWindow(escapeWindow);
                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getHelpWindow());
                return true;
            }
        });

        credits.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ActorUtil.fadeOutWindow(escapeWindow);
                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getCreditsWindow());
                return true;
            }
        });

        settings.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ActorUtil.fadeOutWindow(escapeWindow);
                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getMainSettingsWindow());
                return true;
            }
        });

        logout.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.clientConnection.logout();
                return true;
            }
        });

        exitGame.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Valenguard.clientConnection.disconnect();
                Gdx.app.exit();
                return true;
            }
        });

        returnToGame.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ActorUtil.fadeOutWindow(escapeWindow);
                return true;
            }
        });


        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
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
}
