package com.valenguard.client.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.stage.ConnectionMessageUI;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.EventBus;
import com.valenguard.client.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import lombok.Getter;

public class ClientConnection {

    private static ClientConnection instance;

    @Getter
    private final EventBus eventBus = new EventBus();
    private final int SECONDS_TO_TIMEOUT = 10;
    @Getter
    private ClientHandler clientHandler;

    @Getter
    private boolean connected;

    private ClientConnection() {
    }

    /**
     * Gets the main instance of this class.
     *
     * @return A singleton instance of this class.
     */
    public static ClientConnection getInstance() {
        if (instance == null) instance = new ClientConnection();
        return instance;
    }

    /**
     * Attempts to establish a connection with the server.
     *
     * @param playerSession     The clients provided playerSession details.
     * @param address           The address of the remote server we want to connect to.
     * @param port              The port of the remote server.
     * @param registerListeners Packets that we will listen for from the server.
     */
    public void openConnection(final PlayerSession playerSession, final String address, final short port, final Consumer<EventBus> registerListeners) {
        Log.println(getClass(), "TODO: User player session! UN: " + playerSession.getUsername() + ", PW: " + playerSession.getPassword());
        Log.println(getClass(), "Attempting network connection...");
        threadSafeConnectionMessage("Attempting network connection...", Color.YELLOW);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(address, port), 1000 * SECONDS_TO_TIMEOUT);
                } catch (SocketTimeoutException e) {
                    Log.println(getClass(), "Failed to connect! SocketTimeoutException");
                    threadSafeConnectionMessage("Failed to connect! SocketTimeoutException", Color.RED);
                    closeConnection();
                    return;
                } catch (IOException e) {
                    // Failed to openConnection
                    if (e instanceof ConnectException) {
                        Log.println(getClass(), "Failed to connect! IOException");
                        threadSafeConnectionMessage("Failed to connect! IOException", Color.RED);
                        closeConnection();
                        return;
                    } else {
                        e.printStackTrace();
                    }
                }

                connected = true;

                registerListeners.accept(eventBus);
                receivePackets(socket);

            }
        }, "Connection").start();
    }

    /**
     * Attempt to open up an object input and output stream via the socket.
     *
     * @param socket The connection to the remote server.
     */
    private void receivePackets(Socket socket) {
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;

        // Try to establish a input and output streams.
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (SocketException e1) {
            Log.println(getClass(), "The server appears to be down! SocketException");
            threadSafeConnectionMessage("The server appears to be down! SocketException", Color.RED);
            closeConnection();
            return;

        } catch (SocketTimeoutException e2) {
            Log.println(getClass(), "Connection to the server has timed out! SocketTimeoutException");
            threadSafeConnectionMessage("Connection to the server has timed out! SocketTimeoutException", Color.RED);
            closeConnection();
            return;

        } catch (IOException e3) {
            Log.println(getClass(), "Could not connect to server! IOException");
            threadSafeConnectionMessage("Could not connect to server! IOException", Color.RED);
            closeConnection();
            return;
        }

        // Create our server handler
        clientHandler = new ClientHandler(socket, outputStream, inputStream);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.println(getClass(), "Connection established! Receiving packets!");
                threadSafeConnectionMessage("Connection established! Receiving packets!", Color.GREEN);
                while (connected) {
                    try {
                        eventBus.decodeListenerOnNetworkThread(clientHandler.getInputStream().readByte(), clientHandler);
                    } catch (IOException e) {
                        // Socket closed
                        if (!(e instanceof SocketException && !connected)) {
                            closeConnection();
                            break;
                        }
                    }
                }
            }
        }, "receive_packets").start();

        // TODO:  Can start sending packets here? (REMOVE)
    }

    /**
     * Safely closes a network connection.
     */
    private void closeConnection() {
        Log.println(getClass(), "Closing network connection.");

        connected = false;
        if (clientHandler != null) clientHandler.closeConnection();
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Valenguard.getInstance().setScreen(ScreenType.LOGIN);
                    }
                }, 5);
            }
        });
    }

    /**
     * Sends a info message to our login screen if we are currently on it.
     * Run the following code in a LibGDX thread.
     *
     * @param infoMessage The message we want to send.
     * @param color       The color of the message we are sending.
     */
    public void threadSafeConnectionMessage(final String infoMessage, final Color color) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Valenguard.getInstance().getUiManager().show(new ConnectionMessageUI(infoMessage, color));
            }
        });
    }
}
