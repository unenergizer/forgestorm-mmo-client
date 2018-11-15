package com.valenguard.client.game.screens.stage.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.screens.stage.AbstractUI;
import com.valenguard.client.network.PlayerSession;

public class LoginScreenUI extends AbstractUI implements Disposable {

    private static final boolean DEBUG_STAGE = false;

    private TextField accountField = null;
    private TextField passwordField = null;

    @Override
    public void build(Skin skin) {
        /*
         * SETUP LOGIN
         */
        Table loginTable = new Table();
        loginTable.setFillParent(true);
        loginTable.setDebug(DEBUG_STAGE);
        loginTable.setColor(Color.RED);
        addActor(loginTable);

        // create login widgets
        Valenguard.getInstance().getFileManager().loadTexture(GameTexture.LOGO_BIG);
        Image logo = new Image(Valenguard.getInstance().getFileManager().getTexture(GameTexture.LOGO_BIG));
        Label accountLabel = new Label("Username", skin);
        Label passwordLabel = new Label("Password", skin);

        // If the account field existed before a stage rebuild, keep its contents.
        // Basically if the player tries to connect but fails, keep their login id.
        if (accountField == null) {
            accountField = new TextField(null, skin);
            accountField.setFocusTraversal(false);
            accountField.setMaxLength(12);
        }

        passwordField = new TextField(null, skin);
        passwordField.setFocusTraversal(false);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('#');
        passwordField.setMaxLength(16);

        TextButton loginButton = new TextButton("Login", skin);
        loginButton.pad(3, 10, 3, 10);

        // addUi widgets to table
        loginTable.add(logo).colspan(2).pad(0, 0, 30, 0);
        loginTable.row().pad(10);
        loginTable.add(accountLabel);
        loginTable.add(accountField).uniform();
        loginTable.row().pad(10);
        loginTable.add(passwordLabel);
        loginTable.add(passwordField).uniform();
        loginTable.row().pad(10);
        loginTable.add(loginButton).colspan(2).center().width(150);
        loginTable.row().pad(10);

        // setup event listeners
        accountField.setTextFieldListener(new AccountInput());
        passwordField.setTextFieldListener(new PasswordInput());

        // login to network
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attemptLogin();
            }
        });

        /*
         * SETUP VERSION
         */
        Table versionTable = new Table();
        versionTable.setFillParent(true);
        versionTable.setDebug(DEBUG_STAGE);
        addActor(versionTable);

        // create version widgets
        Label versionLabel = new Label("ClientConnection version " + ClientConstants.GAME_VERSION, skin);
        versionLabel.setFontScale(.8f);

        // init client version in lower left hand corner
        versionTable.add(versionLabel).expand().bottom().left().pad(10);

        /*
         * SETUP COPYRIGHT NOTICE
         */
        Table copyrightTable = new Table();
        copyrightTable.setFillParent(true);
        copyrightTable.setDebug(DEBUG_STAGE);
        addActor(copyrightTable);

        // create copyright widgets
        Label copyrightLabel = new Label("Copyright © 2017-2018 Valenguard MMO. All Rights Reserved.", skin);
        copyrightLabel.setFontScale(.8f);

        // init client version in bottom middle of the screen
        copyrightTable.add(copyrightLabel).expand().center().bottom().pad(10);

        /*
         *  SETUP HELP BUTTONS
         */
        Table buttonTable = new Table();
        buttonTable.setFillParent(false);
        buttonTable.setDebug(DEBUG_STAGE);
        addActor(buttonTable);

        Table buttonTableWrapper = new Table();
        buttonTableWrapper.setFillParent(true);
        buttonTableWrapper.setDebug(DEBUG_STAGE);
        addActor(buttonTableWrapper);

        // create help widgets
        TextButton registerButton = new TextButton("New Account", skin);
        registerButton.pad(3, 10, 3, 10);
        TextButton forgotPasswordButton = new TextButton("Forgot Password", skin);
        forgotPasswordButton.pad(3, 10, 3, 10);
        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.pad(3, 10, 3, 10);
        TextButton exitButton = new TextButton("Exit", skin);
        registerButton.pad(3, 10, 3, 10);

        float buttonWidth = 150;

        // init help buttons in lower right hand corner
        buttonTable.add(registerButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(forgotPasswordButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(settingsButton).pad(3, 0, 3, 0).width(buttonWidth);
        buttonTable.row();
        buttonTable.add(exitButton).pad(3, 0, 3, 0).width(buttonWidth);

        // addUi button table to the button table wrapper
        buttonTableWrapper.add(buttonTable).expand().bottom().right().pad(10);

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

        // exit the game
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void dispose() {
        Valenguard.getInstance().getFileManager().unloadAsset(GameTexture.LOGO_BIG.getFilePath());
    }

    /**
     * This will start Netty and attempt to login to the network.
     */
    private void attemptLogin() {
        Gdx.input.setOnscreenKeyboardVisible(false); // closeConnection the android keyboard

        // TODO: Use these details for user authentication
        String username = accountField.getText();
        String password = passwordField.getText();

        // Clear password filed.
        passwordField.setText("");

        if (!Valenguard.clientConnection.isConnected())
            Valenguard.getInstance().initializeNetwork(new PlayerSession(username, password));
    }

    /*****************************************************************
     * !!! TEXT FIELD LISTENERS !!!
     *
     * WARNING!!! Watch for following characters...
     * Backspace = \b
     * Enter = \n
     * Tab = \t
     */
    private class AccountInput implements TextField.TextFieldListener {
        @Override
        public void keyTyped(TextField textField, char c) {
            // user hit enter/tab/etc, lets playerMove to next text field
            if (c == '\n' || c == '\r' || c == '\t') {
                // TODO: EVALUATE FOR BUGS!
                Valenguard.getInstance().getUiManager().getStage().setKeyboardFocus(passwordField);
            }
        }
    }

    private class PasswordInput implements TextField.TextFieldListener {
        @Override
        public void keyTyped(TextField textField, char c) {
            if (c == '\t') return; // cancel tab
            if (c == '\n' || c == '\r') { // user hit enter, try login
                attemptLogin();
            }
        }
    }
}
