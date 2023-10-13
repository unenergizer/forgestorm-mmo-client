package com.forgestorm.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.io.type.GameTexture;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.*;
import lombok.Getter;

@Getter
public class LoginTable extends VisTable implements Buildable, Disposable {

    private static final String SAVE_USERNAME = "saveUsername";
    private static final String USERNAME = "username";

    private StageHandler stageHandler;
    private final VisTextField usernameField = new VisTextField();
    private final VisTextField passwordField = new VisTextField();
    private final VisTextButton loginButton = new VisTextButton("");
    private final Preferences appPreferences = Gdx.app.getPreferences("RetroMMO-LoginInfo");

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;

        VisTable mainTable = new VisTable(true);
        VisTable logoTable = new VisTable(true);
        HideableVisWindow loginWindow = new HideableVisWindow(stageHandler.getClientMain(), "");
        loginWindow.pad(3);
        loginWindow.setMovable(false);
        final VisTable loginTable = new VisTable(true);

        // create login widgets
        Texture logoTexture = stageHandler.getClientMain().getFileManager().getTexture(GameTexture.LOGO_BIG);
        VisImage logoImage = new VisImage(logoTexture);
        logoTable.add(logoImage).minSize(logoTexture.getWidth(), logoTexture.getHeight()).maxSize(logoTexture.getWidth(), logoTexture.getHeight());
        VisLabel accountLabel = new VisLabel("Username");
        VisLabel passwordLabel = new VisLabel("Password");

        // If the account field existed before a stage rebuild, keep its contents.
        // Basically if the player tries to connect but fails, keep their login id.
        usernameField.setFocusTraversal(false);
        usernameField.setMaxLength(12);

        passwordField.setFocusTraversal(false);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('#');
        passwordField.setMaxLength(16);

        // Set prefilled credentials
        usernameField.setText(stageHandler.getClientMain().getLoginCredentials().getUsername());
        passwordField.setText(stageHandler.getClientMain().getLoginCredentials().getPassword());

        loginButton.setText("Login");

        // Username/Password Preferences
        boolean saveUsername = appPreferences.getBoolean(SAVE_USERNAME, false);

        if (saveUsername && usernameField.isEmpty()) {
            String username = appPreferences.getString(USERNAME, "");
            usernameField.setText(username);
        }

        final VisCheckBox usernameCheckBox = new VisCheckBox("Save Username", saveUsername);


        // addUi widgets to table
        loginTable.add(accountLabel);
        loginTable.add(usernameField).uniform();
        loginTable.row();
        loginTable.add(passwordLabel);
        loginTable.add(passwordField).uniform();
        loginTable.row();
        loginTable.add(loginButton).colspan(2).center().width(150);
        loginTable.row();
        loginTable.add(usernameCheckBox).colspan(2).align(Alignment.CENTER.getAlignment());
        loginWindow.add(loginTable).pad(10);

        mainTable.add(logoTable);
        mainTable.row();
        mainTable.add(loginWindow);
        add(mainTable);

        // setup event listeners
        usernameField.setTextFieldListener(new AccountInput());
        passwordField.setTextFieldListener(new PasswordInput());

        usernameField.setCursorAtTextEnd();

        // login to network
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (usernameCheckBox.isChecked()) {
                    appPreferences.putString(USERNAME, usernameField.getText());
                    appPreferences.flush();
                }
                attemptLogin();
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(LoginTable.class, (short) 11);
            }
        });

        // Preferences Listener
        usernameCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (usernameCheckBox.isChecked()) {
                    appPreferences.putBoolean(SAVE_USERNAME, true);
                    if (!usernameField.isEmpty())
                        appPreferences.putString(USERNAME, usernameField.getText());
                    appPreferences.flush();
                } else {
                    appPreferences.putBoolean(SAVE_USERNAME, false);
                    appPreferences.putString(USERNAME, "");
                    appPreferences.flush();
                }
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(LoginTable.class, (short) 0);
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((float) (Gdx.graphics.getWidth() / 2) - (getWidth() / 2), (float) (Gdx.graphics.getHeight() / 2) - (getHeight() / 2));
            }
        });

        setPosition((float) (Gdx.graphics.getWidth() / 2) - (getWidth() / 2), (float) (Gdx.graphics.getHeight() / 2) - (getHeight() / 2));
        setVisible(false);
        return this;
    }

    @Override
    public void dispose() {
        stageHandler.getClientMain().getFileManager().unloadAsset(GameTexture.LOGO_BIG.getFilePath());
    }

    /**
     * This will start Netty and attempt to login to the network.
     */
    private void attemptLogin() {
        Gdx.input.setOnscreenKeyboardVisible(false); // logout the android keyboard

        // TODO: Use these details for user authentication
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Clear password filed.
        passwordField.setText("");
        loginButton.setDisabled(true);
        loginButton.setText("Logging in...");

        if (!stageHandler.getClientMain().getConnectionManager().getClientGameConnection().isConnected()) {
            stageHandler.getClientMain().getLoginCredentials().setUsername(username);
            stageHandler.getClientMain().getLoginCredentials().setPassword(password);
            stageHandler.getClientMain().getConnectionManager().connect();
        }
    }

    public void resetButton() {
        loginButton.setDisabled(false);
        loginButton.setText("Login");
        passwordField.setText(stageHandler.getClientMain().getLoginCredentials().getPassword());
    }

    /*****************************************************************
     * !!! TEXT FIELD LISTENERS !!!
     * WARNING!!! Watch for following characters...
     * Backspace = \b
     * Enter = \n
     * Tab = \t
     */
    private class AccountInput implements VisTextField.TextFieldListener {
        @Override
        public void keyTyped(VisTextField textField, char c) {
            // user hit enter/tab/etc, lets move to password text field
            if (c == '\n' || c == '\r' || c == '\t') {
                FocusManager.switchFocus(stageHandler.getStage(), passwordField);
                stageHandler.getStage().setKeyboardFocus(passwordField);
                passwordField.setCursorAtTextEnd();
            }
        }
    }

    private class PasswordInput implements VisTextField.TextFieldListener {
        @Override
        public void keyTyped(VisTextField textField, char c) {
            if (c == '\t') {
                // user hit tab, lets playerMove to account text field
                FocusManager.switchFocus(stageHandler.getStage(), usernameField);
                stageHandler.getStage().setKeyboardFocus(usernameField);
            } else if (c == '\n' || c == '\r') {
                // user hit enter, try login
                attemptLogin();
            }
        }
    }
}
