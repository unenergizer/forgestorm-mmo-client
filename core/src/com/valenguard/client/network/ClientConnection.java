package com.valenguard.client.network;

import com.badlogic.gdx.Gdx;
import com.valenguard.client.Valenguard;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.EventBus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

@Getter
public class ClientConnection {

    private final EventBus eventBus = new EventBus();
    private final int SECONDS_TO_TIMEOUT = 10;

    @Setter
    private long ping = 0;
    private ClientHandler clientHandler;
    private boolean connected;

    /**
     * Attempts to establish a connection with the server.
     *
     * @param playerSession     The clients provided playerSession details.
     * @param address           The address of the remote server we want to connect to.
     * @param port              The port of the remote server.
     * @param registerListeners Packets that we will listen for from the server.
     */
    public void openConnection(final PlayerSession playerSession, final String address, final short port, final Consumer<EventBus> registerListeners) {
        println(ClientConnection.class, "TODO: User player session! UN: " + playerSession.getUsername() + ", PW: " + playerSession.getPassword());
        println(ClientConnection.class, "Attempting network connection...");
        threadSafeConnectionMessage("Attempting network connection...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(address, port), 1000 * SECONDS_TO_TIMEOUT);
                } catch (SocketTimeoutException e) {
                    println(ClientConnection.class, "Failed to connect! SocketTimeoutException");
                    threadSafeConnectionMessage("Failed to connect! SocketTimeoutException");
                    logout();
                    return;
                } catch (IOException e) {
                    // Failed to openConnection
                    if (e instanceof ConnectException) {
                        println(ClientConnection.class, "Failed to connect! IOException");
                        threadSafeConnectionMessage("Failed to connect! IOException");
                        logout();
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
        DataInputStream inputStream;
        DataOutputStream outputStream;

        // Try to establish a input and output streams.
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (SocketException e1) {
            println(ClientConnection.class, "The server appears to be down! SocketException");
            threadSafeConnectionMessage("The server appears to be down! SocketException");
            logout();
            return;

        } catch (SocketTimeoutException e2) {
            println(ClientConnection.class, "Connection to the server has timed out! SocketTimeoutException");
            threadSafeConnectionMessage("Connection to the server has timed out! SocketTimeoutException");
            logout();
            return;

        } catch (IOException e3) {
            println(ClientConnection.class, "Could not connect to server! IOException");
            threadSafeConnectionMessage("Could not connect to server! IOException");
            logout();
            return;
        }

        // Create our server handler
        clientHandler = new ClientHandler(socket, outputStream, inputStream);

        new Thread(new Runnable() {
            @Override
            public void run() {
                println(ClientConnection.class, "Connection established! Receiving packets!");
                threadSafeConnectionMessage("Connection established! Receiving packets!");
                while (connected) {
                    try {

                        byte opcodeByte = clientHandler.getInputStream().readByte();
                        byte numberOfRepeats = 1;
                        if (((opcodeByte >>> 8) & 0x01) != 0) {

                            // Removing the special bit.
                            opcodeByte = (byte) (opcodeByte & 0x7F);
                            numberOfRepeats = clientHandler.getInputStream().readByte();
                        }

                        for (byte i = 0; i < numberOfRepeats; i++)
                            eventBus.decodeListenerOnNetworkThread(opcodeByte, clientHandler);

                    } catch (NullPointerException e) {
                        // Socket closed
                        println(ClientConnection.class, "Tried to read data, but socket closed!", true);
                    } catch (IOException e) {
                        // Socket closed
                        if (!(e instanceof SocketException && !connected)) {
                            logout();
                            break;
                        }
                    }
                }
            }
        }, "receive_packets").start();

        // TODO:  Can start sending packets here? (REMOVE)
    }

    public static void main(String args[]) {
        byte b1 = (byte) 0x7D;
        String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
        System.out.println(s1); // 10000001
    }


    /**
     * Sends the player back to the login screen.
     */
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
     * Safely closes a network connection.
     */
    public void disconnect() {
        println(ClientConnection.class, "Closing network connection.");
        connected = false;
        if (clientHandler != null) clientHandler.closeConnection();
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
                Valenguard.getInstance().getStageHandler().getConnectionStatusWindow().setStatusMessage(infoMessage);
            }
        });
    }
}
