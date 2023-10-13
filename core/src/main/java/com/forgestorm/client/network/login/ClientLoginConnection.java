package com.forgestorm.client.network.login;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.network.ConnectionManager;
import com.forgestorm.client.network.game.LoginCredentials;
import com.forgestorm.shared.network.login.LoginFailReason;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.UUID;

import static com.forgestorm.client.util.Log.println;

public class ClientLoginConnection {

    private final ClientMain clientMain;
    private final ConnectionManager connectionManager;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;


    public ClientLoginConnection(ClientMain clientMain, final ConnectionManager connectionManager) {
        this.clientMain = clientMain;
        this.connectionManager = connectionManager;
    }

    public boolean openConnection(String address, final int port) {

        // Check if local host run
        boolean forceLocalHost = clientMain.isForceLocalHost();
        if (forceLocalHost) address = "localhost";

        println(getClass(), "Connecting to: " + address + ":" + port);

        try {
//            socket = SSLSocketFactory.getDefault().createSocket();
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 1000 * ClientConstants.NETWORK_SECONDS_TO_TIMEOUT);
        } catch (SocketTimeoutException e) {
            println(ClientLoginConnection.class, "Failed to connect! SocketTimeoutException");
            connectionManager.threadSafeConnectionMessage("Failed to connect! SocketTimeoutException");
            connectionManager.logout();
            return false;
        } catch (IOException e) {
            // Failed to openConnection
            if (e instanceof ConnectException) {
                println(ClientLoginConnection.class, "Failed to connect! IOException");
                connectionManager.threadSafeConnectionMessage("Failed to connect! IOException");
                connectionManager.logout();
                return false;
            } else {
                e.printStackTrace();
            }
        }

        // Try to establish a input and output streams.
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (SocketException e1) {
            println(ClientLoginConnection.class, "(WRONG MSG) The server appears to be down! SocketException");
            connectionManager.threadSafeConnectionMessage("(WRONG MSG) The server appears to be down! SocketException");
            connectionManager.logout();
            return false;

        } catch (SocketTimeoutException e2) {
            println(ClientLoginConnection.class, "(WRONG MSG) Connection to the server has timed out! SocketTimeoutException");
            connectionManager.threadSafeConnectionMessage("(WRONG MSG) Connection to the server has timed out! SocketTimeoutException");
            connectionManager.logout();
            return false;

        } catch (IOException e3) {
            println(ClientLoginConnection.class, "(WRONG MSG) Could not connect to server! IOException");
            connectionManager.threadSafeConnectionMessage("(WRONG MSG) Could not connect to server! IOException");
            connectionManager.logout();
            return false;
        }

        return true;
    }

    public LoginState authenticate(LoginCredentials loginCredentials) {

        LoginState loginState = new LoginState();
        try {
            outputStream.writeUTF(loginCredentials.getUsername());
            outputStream.writeUTF(loginCredentials.getPassword());

            boolean loginSuccess = inputStream.readBoolean();
            if (!loginSuccess) {
                LoginFailReason loginFailReason = LoginFailReason.getLoginFailReason(inputStream.readByte());
                println(getClass(), "Login Failed: " + loginFailReason.getFailReasonMessage());
                loginState.failState(loginFailReason);
            } else {
                println(getClass(), "Login Success");
                loginState.successState(UUID.fromString(inputStream.readUTF()));
            }

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return new LoginState().failState(LoginFailReason.FAILED_TO_CONNECT);
        }

        // Login state is now completed. Close the sockets.
        disconnect();
        return loginState;
    }

    private void disconnect() {
        println(ClientLoginConnection.class, "Closing network connection.");
        try {
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            socket = null;
            inputStream = null;
            outputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
