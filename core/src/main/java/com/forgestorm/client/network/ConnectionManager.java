package com.forgestorm.client.network;

import com.badlogic.gdx.Gdx;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.network.game.ClientGameConnection;
import com.forgestorm.client.network.game.Consumer;
import com.forgestorm.client.network.game.LoginCredentials;
import com.forgestorm.client.network.game.packet.out.OutputStreamManager;
import com.forgestorm.client.network.game.shared.EventBus;
import com.forgestorm.client.network.login.ClientLoginConnection;
import com.forgestorm.client.network.login.LoginState;
import com.forgestorm.shared.io.NetworkSettingsLoader;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class ConnectionManager {

    private final NetworkSettingsLoader.NetworkSettingsData networkSettingsData;
    private final LoginCredentials loginCredentials;
    private final Consumer<EventBus> registerListeners;

    @Getter
    private final ClientGameConnection clientGameConnection = new ClientGameConnection(this);
    private final ClientLoginConnection clientLoginConnection = new ClientLoginConnection(this);

    private NetworkThread networkThread;
    private int networkThreadsCreated = 0;

    @Getter
    private OutputStreamManager outputStreamManager;

    public ConnectionManager(final NetworkSettingsLoader.NetworkSettingsData networkSettingsData, final LoginCredentials loginCredentials, final Consumer<EventBus> registerListeners) {
        println(getClass(), "Force LocalHost: " + ClientMain.getInstance().isForceLocalHost());
        this.networkSettingsData = networkSettingsData;
        this.loginCredentials = loginCredentials;
        this.registerListeners = registerListeners;
    }

    /**
     * Creates a new {@link NetworkThread} to authenticate the user and then send/receive packets.
     */
    public void connect() {
        if (networkThread != null) {
            throw new RuntimeException("Trying to create a new network thread.");
        }

        outputStreamManager = new OutputStreamManager();

        networkThreadsCreated++;
        networkThread = new NetworkThread(networkThreadsCreated);
        networkThread.start();
    }

    /**
     * Called when we want to end the {@link NetworkThread}.
     */
    public void disconnect() {
        if (outputStreamManager != null) {
            outputStreamManager.dispose();
            outputStreamManager = null;
        }

        clientGameConnection.disconnect();

        if (networkThread != null) {
            networkThread.interrupt();
            networkThread = null;
        }
    }

    /**
     * Called when the player logs out of the game network.
     */
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

    /**
     * This class contains the main network loop.
     */
    private class NetworkThread extends Thread {

        NetworkThread(final int initCount) {
            setName("NetworkThread number " + initCount);
        }

        @Override
        public void run() {
            if (!clientLoginConnection.openConnection(networkSettingsData.getLoginIp(), networkSettingsData.getLoginPort())) {
                return;
            }

            LoginState loginState = clientLoginConnection.authenticate(loginCredentials);

            if (!loginState.getLoginSuccess()) {
                threadSafeConnectionMessage(loginState.getLoginFailReason().getFailReasonMessage());
                logout();
                return;
            }

            clientGameConnection.openConnection(
                    loginState.getUuid(),
                    networkSettingsData.getGameIp(),
                    networkSettingsData.getGamePort(),
                    registerListeners);
        }
    }
}
