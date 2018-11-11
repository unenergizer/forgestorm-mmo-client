package com.valenguard.client.network.packet.in;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.network.ClientConnection;
import com.valenguard.client.network.PlayerSessionData;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;
import com.valenguard.client.util.Log;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.INIT_CLIENT_SESSION)
public class InitializeClientSession implements PacketListener<InitializeClientSession.InitClientSessionPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new InitClientSessionPacket(clientHandler.readBoolean(), clientHandler.readShort());
    }

    @Override
    public void onEvent(InitClientSessionPacket packetData) {

        final ClientConnection client = ClientConnection.getInstance();

        // TODO: Check for authentication

        // Network connection was successful.
        Log.println(getClass(), "Connection successful!");
        client.threadSafeConnectionMessage("Connection successful!", Color.GREEN);

        // PACKET READ START


        Log.println(getClass(), "LoginSuccess: " + packetData.loginSuccess
                + " , Session Player Id: " + packetData.clientPlayerId);

        // Set map to show client
        Valenguard.gameScreen.setPlayerSessionData(new PlayerSessionData(packetData.clientPlayerId));
        // TODO: Remove this call here and put in game screen. Use client player data to get map name.
        Valenguard.getInstance().setScreen(ScreenType.GAME);

    }

    @AllArgsConstructor
    class InitClientSessionPacket extends PacketData {
        private boolean loginSuccess;
        private short clientPlayerId;
    }
}
