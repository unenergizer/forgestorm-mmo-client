package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.audio.MusicManager;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.settings.MainSettingsWindow;

public class ButtonTable extends VisTable implements Buildable {

    @Override
    public Actor build() {
        VisTable buttonTable = new VisTable();


        /*
         * Play Login Screen Music Toggle
         */
        VisTable musicTable = new VisTable();
        final MusicManager musicManager = Valenguard.getInstance().getMusicManager();
        final VisCheckBox playLoginMusicRadioButton = new VisCheckBox("");
        playLoginMusicRadioButton.setChecked(musicManager.getAudioPreferences().isPlayLoginScreenMusic());
        musicTable.add(new VisLabel("Play Music")).padRight(3).right();
        musicTable.add(playLoginMusicRadioButton);

        // create help widgets
        VisTextButton registerButton = new VisTextButton("New Account");
        VisTextButton forgotPasswordButton = new VisTextButton("Forgot Password");
        VisTextButton settingsButton = new VisTextButton("Settings");
        VisTextButton exitButton = new VisTextButton("Exit");

        float buttonWidth = 150;

        // init help buttons in lower right hand corner
        buttonTable.add(musicTable).pad(3, 0, 3, 0).width(buttonWidth).align(Align.right);
        buttonTable.row();
        buttonTable.add(registerButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(forgotPasswordButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(settingsButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(exitButton).pad(3, 0, 3, 0).width(buttonWidth);

        add(buttonTable);

        // stops or plays music
        playLoginMusicRadioButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicManager.getAudioPreferences().setPlayLoginScreenMusic(playLoginMusicRadioButton.isChecked());
                if (!playLoginMusicRadioButton.isChecked()) {
                    musicManager.stopSong(true);
                    musicManager.getAudioPreferences().setPlayLoginScreenMusic(false);
                } else {
                    musicManager.playSong(musicManager.getLastPlayedSong());
                    musicManager.getAudioPreferences().setPlayLoginScreenMusic(true);
                }
                event.handle();
            }
        });

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
