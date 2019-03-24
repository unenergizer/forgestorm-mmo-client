package com.valenguard.client.network;

import com.badlogic.gdx.Gdx;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.network.game.ClientGameConnection;
import com.valenguard.client.network.game.Consumer;
import com.valenguard.client.network.game.LoginCredentials;
import com.valenguard.client.network.game.shared.EventBus;
import com.valenguard.client.network.login.ClientLoginConnection;
import com.valenguard.client.network.login.LoginState;

import lombok.Getter;

public class ConnectionManager {

    private ClientLoginConnection clientLoginConnection = new ClientLoginConnection(this);

    @Getter
    private ClientGameConnection clientGameConnection = new ClientGameConnection(this);

    public void setupConnection(final NetworkSettings networkSettings, final LoginCredentials loginCredentials, final Consumer<EventBus> registerListeners) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!clientLoginConnection.openConnection(networkSettings.getLoginIp(), networkSettings.getLoginPort())) {
                    return;
                }

                LoginState loginState = clientLoginConnection.authenticate(loginCredentials);

                if (!loginState.getLoginSuccess()) {
                    threadSafeConnectionMessage(loginState.getFailReason());
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
        }, "Connection").start();

    }

    public void logout() {
        disconnect();

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Valenguard.getInstance().dispose();
                Valenguard.getInstance().create();
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
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ActorUtil.getStageHandler().getConnectionStatusWindow().setStatusMessage(infoMessage);
            }
        });
    }

    public void disconnect() {
        clientLoginConnection.disconnect();
        clientGameConnection.disconnect();
    }
}
