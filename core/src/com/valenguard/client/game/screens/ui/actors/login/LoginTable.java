package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.screens.ui.Buildable;
import com.valenguard.client.game.screens.ui.HideableVisWindow;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.network.PlayerSession;

public class LoginTable extends VisTable implements Buildable, Disposable {

    private StageHandler stageHandler;
    private VisTextField accountField = null;
    private VisTextField passwordField = null;

    public LoginTable(StageHandler stageHandler) {
//        super("");
        this.stageHandler = stageHandler;
    }

    @Override
    public Actor build() {
        VisTable mainTable = new VisTable(true);
        VisTable logoTable = new VisTable(true);
        HideableVisWindow loginWindow = new HideableVisWindow("Game Login:");
        loginWindow.setMovable(false);
        VisTable loginTable = new VisTable(true);

        // create login widgets
        Valenguard.getInstance().getFileManager().loadTexture(GameTexture.LOGO_BIG);
        Texture logoTexture = Valenguard.getInstance().getFileManager().getTexture(GameTexture.LOGO_BIG);
        VisImage logoImage = new VisImage(logoTexture);
        logoTable.add(logoImage).minSize(logoTexture.getWidth(), logoTexture.getHeight()).maxSize(logoTexture.getWidth(), logoTexture.getHeight());
        VisLabel accountLabel = new VisLabel("Username");
        VisLabel passwordLabel = new VisLabel("Password");

        // If the account field existed before a stage rebuild, keep its contents.
        // Basically if the player tries to connect but fails, keep their login id.
        if (accountField == null) {
            accountField = new VisTextField(null);
            accountField.setFocusTraversal(false);
            accountField.setMaxLength(12);
        }

        passwordField = new VisTextField(null);
        passwordField.setFocusTraversal(false);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('#');
        passwordField.setMaxLength(16);

        VisTextButton loginButton = new VisTextButton("Login");

        // addUi widgets to table
        loginTable.add(accountLabel);
        loginTable.add(accountField).uniform();
        loginTable.row();
        loginTable.add(passwordLabel);
        loginTable.add(passwordField).uniform();
        loginTable.row();
        loginTable.add(loginButton).colspan(2).center().width(150);
        loginWindow.add(loginTable).pad(10);

        mainTable.add(logoTable);
        mainTable.row();
        mainTable.add(loginWindow);
        add(mainTable);

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

        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (getHeight() / 2));
        setVisible(false);
        return this;
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

        if (!Valenguard.clientConnection.isConnected()) {
            Valenguard.getInstance().initializeNetwork(new PlayerSession(username, password));
        }
    }

    /*****************************************************************
     * !!! TEXT FIELD LISTENERS !!!
     *
     * WARNING!!! Watch for following characters...
     * Backspace = \b
     * Enter = \n
     * Tab = \t
     */
    private class AccountInput implements VisTextField.TextFieldListener {
        @Override
        public void keyTyped(VisTextField textField, char c) {
            // user hit enter/tab/etc, lets playerMove to next text field
            if (c == '\n' || c == '\r' || c == '\t') {
                FocusManager.switchFocus(stageHandler.getStage(), passwordField);
            }
        }
    }

    private class PasswordInput implements VisTextField.TextFieldListener {
        @Override
        public void keyTyped(VisTextField textField, char c) {
            if (c == '\t') return; // cancel tab
            if (c == '\n' || c == '\r') { // user hit enter, try login
                attemptLogin();
            }
        }
    }
}
