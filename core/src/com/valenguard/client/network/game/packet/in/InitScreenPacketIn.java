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

@Opcode(getOpcode = Opcodes.INIT_SCREEN)
public class InitScreenPacketIn implements PacketListener<InitScreenPacketIn.InitCharacterSessionPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        ScreenType screenType = ScreenType.getScreenType(clientHandler.readByte());
        short playerClientId = -1;

        println(getClass(), "ScreenSwitch: " + screenType, false);

        switch (screenType) {
            case LOGIN:
                break;
            case CHARACTER_SELECT:
                break;
            case GAME:
                playerClientId = clientHandler.readShort();
                break;
        }

        return new InitCharacterSessionPacket(screenType, playerClientId);
    }

    @Override
    public void onEvent(InitCharacterSessionPacket packetData) {

        switch (packetData.screenType) {
            case LOGIN:
                // TODO: Character logged out, send to login screen
                break;
            case CHARACTER_SELECT:
                // Network connection was successful.
                Valenguard.connectionManager.threadSafeConnectionMessage("Connection successful!");
                Valenguard.getInstance().setScreen(ScreenType.CHARACTER_SELECT);
                break;
            case GAME:
                println(getClass(), "Session Player Id: " + packetData.clientPlayerId, false, PRINT_DEBUG);
                Valenguard.gameScreen.setPlayerSessionData(new PlayerSessionData(packetData.clientPlayerId));
                Valenguard.getInstance().setScreen(ScreenType.GAME);
                break;
        }
    }

    @AllArgsConstructor
    class InitCharacterSessionPacket extends PacketData {
        private ScreenType screenType;
        private short clientPlayerId;
    }
}
