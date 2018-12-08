package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.settings.MainSettingsWindow;

public class ButtonTable extends VisTable implements Buildable {

    @Override
    public Actor build() {
        VisTable buttonTable = new VisTable();

        // create help widgets
        VisTextButton registerButton = new VisTextButton("New Account");
        VisTextButton forgotPasswordButton = new VisTextButton("Forgot Password");
        VisTextButton settingsButton = new VisTextButton("Settings");
        VisTextButton exitButton = new VisTextButton("Exit");

        float buttonWidth = 150;

        // init help buttons in lower right hand corner
        buttonTable.add(registerButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(forgotPasswordButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(settingsButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(exitButton).pad(3, 0, 3, 0).width(buttonWidth);

        add(buttonTable);

        // opens up web page for player registration
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI(ClientConstants.WEB_REGISTER);
            }
        });

        // opens up web page to recover lost password
        forgotPasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI(ClientConstants.WEB_LOST_PASSWORD);
            }
        });

        // settings window
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainSettingsWindow mainSettingsWindow = Valenguard.getInstance().getStageHandler().getMainSettingsWindow();
                if (!mainSettingsWindow.isVisible()) mainSettingsWindow.fadeIn().setVisible(true);
                else mainSettingsWindow.fadeOut();
            }
        });

        // exit the game
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        pack();
        setPosition(Gdx.graphics.getWidth() - getWidth() - 10, 10);
        setVisible(false);
        return this;
    }
}
