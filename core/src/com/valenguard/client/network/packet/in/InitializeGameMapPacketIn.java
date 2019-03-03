package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INIT_MAP)
public class InitializeGameMapPacketIn implements PacketListener<InitializeGameMapPacketIn.InitGameMapPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new InitGameMapPacket(clientHandler.readString());
    }

    @Override
    public void onEvent(InitGameMapPacket packetData) {
        println(getClass(), "", false, PRINT_DEBUG);
        println(getClass(), "================== [ NEW MAP INIT ] ===================", false, PRINT_DEBUG);
        println(getClass(), "1. Switching to map: " + packetData.gameMap, false, PRINT_DEBUG);

        println(InitializeGameMapPacketIn.class, "2. Switching to map: " + packetData.gameMap, false, PRINT_DEBUG);
        Valenguard.gameScreen.getMapRenderer().setTiledMap(packetData.gameMap);
        Valenguard.getInstance().getClientMovementProcessor().invalidateAllInput();
    }

    @AllArgsConstructor
    class InitGameMapPacket extends PacketData {
        private String gameMap;
    }
}
