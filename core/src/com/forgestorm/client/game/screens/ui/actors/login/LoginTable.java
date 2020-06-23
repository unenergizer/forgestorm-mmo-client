package com.forgestorm.client.game.screens.ui.actors.login;

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
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.io.type.GameTexture;

import lombok.Getter;

@Getter
public class LoginTable extends VisTable implements Buildable, Disposable {

    private StageHandler stageHandler;
    private VisTextField usernameField = new VisTextField();
    private VisTextField passwordField = new VisTextField();
    private VisTextButton loginButton = new VisTextButton("");

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;

        VisTable mainTable = new VisTable(true);
        VisTable logoTable = new VisTable(true);
        HideableVisWindow loginWindow = new HideableVisWindow("");
        loginWindow.pad(3);
        loginWindow.setMovable(false);
        VisTable loginTable = new VisTable(true);

        // create login widgets
        ClientMain.getInstance().getFileManager().loadTexture(GameTexture.LOGO_BIG);
        Texture logoTexture = ClientMain.getInstance().getFileManager().getTexture(GameTexture.LOGO_BIG);
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
        usernameField.setText(ClientMain.getInstance().getLoginCredentials().getUsername());
        passwordField.setText(ClientMain.getInstance().getLoginCredentials().getPassword());

        loginButton.setText("Login");

        // addUi widgets to table
        loginTable.add(accountLabel);
        loginTable.add(usernameField).uniform();
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
        usernameField.setTextFieldListener(new AccountInput());
        passwordField.setTextFieldListener(new PasswordInput());

        usernameField.setCursorAtTextEnd();

        // login to network
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attemptLogin();
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(LoginTable.class, (short) 0);
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (getHeight() / 2));
            }
        });

        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (getHeight() / 2));
        setVisible(false);
        return this;
    }

    @Override
    public void dispose() {
        ClientMain.getInstance().getFileManager().unloadAsset(GameTexture.LOGO_BIG.getFilePath());
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

        if (!ClientMain.connectionManager.getClientGameConnection().isConnected()) {
            ClientMain.getInstance().getLoginCredentials().setUsername(username);
            ClientMain.getInstance().getLoginCredentials().setPassword(password);
            ClientMain.getInstance().initializeNetwork();
        }
    }

    public void resetButton() {
        loginButton.setDisabled(false);
        loginButton.setText("Login");
        passwordField.setText(ClientMain.getInstance().getLoginCredentials().getPassword());
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
