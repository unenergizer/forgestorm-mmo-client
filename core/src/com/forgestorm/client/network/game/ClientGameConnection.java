package com.forgestorm.client.network.game;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.network.ConnectionManager;
import com.forgestorm.client.network.game.packet.out.ForgeStormOutputStream;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.EventBus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
public class ClientGameConnection {

    private final ConnectionManager connectionManager;
    private final EventBus eventBus = new EventBus();

    @Setter
    private long ping = 0;
    private UUID uuid;
    private ClientHandler clientHandler;
    private boolean connected;

    public ClientGameConnection(final ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Attempts to establish a connection with the server.
     *
     * @param address           The address of the remote server we want to connect to.
     * @param port              The port of the remote server.
     * @param registerListeners Packets that we will listen for from the server.
     */
    public void openConnection(final UUID uuid, final String address, final int port, final Consumer<EventBus> registerListeners) {
        this.uuid = uuid;
        println(getClass(), "Connecting to: " + address + ":" + port);
        println(ClientGameConnection.class, "Attempting network connection...");
        connectionManager.threadSafeConnectionMessage("Attempting network connection..."); // todo: probably move

        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 1000 * ClientConstants.NETWORK_SECONDS_TO_TIMEOUT);
        } catch (SocketTimeoutException e) {
            println(ClientGameConnection.class, "Failed to connect! SocketTimeoutException");
            connectionManager.threadSafeConnectionMessage("Failed to connect! SocketTimeoutException");
            connectionManager.logout();
            return;
        } catch (IOException e) {
            // Failed to openConnection
            if (e instanceof ConnectException) {
                println(ClientGameConnection.class, "Failed to connect! IOException");
                connectionManager.threadSafeConnectionMessage("Failed to connect! IOException");
                connectionManager.logout();
                return;
            } else {
                e.printStackTrace();
            }
        }

        connected = true;

        registerListeners.accept(eventBus);
        receivePackets(socket);

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

            // Send the server our UUID
            outputStream.writeUTF(uuid.toString());

        } catch (SocketException e1) {
            println(ClientGameConnection.class, "The server appears to be down! SocketException");
            connectionManager.threadSafeConnectionMessage("The server appears to be down! SocketException");
            connectionManager.logout();
            return;

        } catch (SocketTimeoutException e2) {
            println(ClientGameConnection.class, "Connection to the server has timed out! SocketTimeoutException");
            connectionManager.threadSafeConnectionMessage("Connection to the server has timed out! SocketTimeoutException");
            connectionManager.logout();
            return;

        } catch (IOException e3) {
            println(ClientGameConnection.class, "Could not connect to server! IOException");
            connectionManager.threadSafeConnectionMessage("Could not connect to server! IOException");
            connectionManager.logout();
            return;
        }

        // Create our server handler
        clientHandler = new ClientHandler(socket, new ForgeStormOutputStream(outputStream), inputStream);

        println(ClientGameConnection.class, "Connection established! Receiving packets!");
        connectionManager.threadSafeConnectionMessage("Connection established! Receiving packets!");
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
                println(ClientGameConnection.class, "Tried to read data, but socket closed!", true);
            } catch (IOException e) {
                // Socket closed
                if (!(e instanceof SocketException && !connected)) {
                    connectionManager.logout();
                    break;
                }
            }
        }
    }

    /**
     * Safely closes a network connection.
     */
    public void disconnect() {
        println(ClientGameConnection.class, "Closing network connection.");
        connected = false;
        if (clientHandler != null) clientHandler.closeConnection();
    }
}
