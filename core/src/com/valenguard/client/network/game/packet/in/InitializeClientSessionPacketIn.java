package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.network.PlayerSessionData;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INIT_CLIENT_SESSION)
public class InitializeClientSessionPacketIn implements PacketListener<InitializeClientSessionPacketIn.InitClientSessionPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final boolean successLogin = clientHandler.readBoolean();
        final short playerClientId = clientHandler.readShort();
        return new InitClientSessionPacket(successLogin, playerClientId);
    }

    @Override
    public void onEvent(InitClientSessionPacket packetData) {

        // TODO: REDO

        // Network connection was successful.
        println(getClass(), "Connection successful!", false, PRINT_DEBUG);
        Valenguard.connectionManager.threadSafeConnectionMessage("Connection successful!");

        println(getClass(), "LoginSuccess: " + packetData.loginSuccess
                + " , Session Player Id: " + packetData.clientPlayerId, false, PRINT_DEBUG);

        Valenguard.gameScreen.setPlayerSessionData(new PlayerSessionData(packetData.clientPlayerId));
        Valenguard.getInstance().setScreen(ScreenType.GAME);
    }

    @AllArgsConstructor
    class InitClientSessionPacket extends PacketData {
        private boolean loginSuccess;
        private short clientPlayerId;
    }
}
