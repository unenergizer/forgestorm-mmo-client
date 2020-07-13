package com.forgestorm.client.network;

import com.badlogic.gdx.Gdx;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.network.game.ClientGameConnection;
import com.forgestorm.client.network.game.Consumer;
import com.forgestorm.client.network.game.LoginCredentials;
import com.forgestorm.client.network.game.shared.EventBus;
import com.forgestorm.client.network.login.ClientLoginConnection;
import com.forgestorm.client.network.login.LoginState;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class ConnectionManager extends Thread {

    private ClientLoginConnection clientLoginConnection = new ClientLoginConnection(this);

    @Getter
    private ClientGameConnection clientGameConnection = new ClientGameConnection(this);

    private NetworkSettings networkSettings;
    private LoginCredentials loginCredentials;
    private Consumer<EventBus> registerListeners;

    public void setupConnection(final NetworkSettings networkSettings, final LoginCredentials loginCredentials, final Consumer<EventBus> registerListeners) {
        println(getClass(), "Force LocalHost: " + ClientMain.getInstance().isForceLocalHost());
        this.networkSettings = networkSettings;
        this.loginCredentials = loginCredentials;
        this.registerListeners = registerListeners;

        this.start();
    }

    public void logout() {
        disconnect();

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ActorUtil.getStageHandler().setUserInterface(UserInterfaceType.LOGIN);
            }
        });
    }

    /**
     * Sends a info message to our login screen if we are currently on it.
     * Run the following code in a LibGDX thread.
     *
     * @param infoMessage The message we want to send.
     */
    public void threadSafeConnectionMessage(final String infoMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        ClientMain.getInstance().getStageHandler().getConnectionStatusWindow().setStatusMessage(infoMessage);
                    }
                });
            }
        }).start();
    }

    @Override
    public void run() {
        if (!clientLoginConnection.openConnection(networkSettings.getLoginIp(), networkSettings.getLoginPort())) {
            return;
        }

        LoginState loginState = clientLoginConnection.authenticate(loginCredentials);

        if (!loginState.getLoginSuccess()) {
            threadSafeConnectionMessage(loginState.getLoginFailReason().getFailReasonMessage());
            logout();
            return;
        }

        clientLoginConnection.disconnect();

        clientGameConnection.openConnection(
                loginState.getUuid(),
                networkSettings.getGameIp(),
                networkSettings.getGamePort(),
                registerListeners);
    }

    public void disconnect() {
        clientLoginConnection.disconnect();
        clientGameConnection.disconnect();
    }

}
