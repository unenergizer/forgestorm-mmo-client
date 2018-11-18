package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;
import com.valenguard.client.util.Log;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.INIT_MAP)
public class InitializeGameMap implements PacketListener<InitializeGameMap.InitGameMapPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new InitGameMapPacket(clientHandler.readString());
    }

    @Override
    public void onEvent(InitGameMapPacket packetData) {
        Log.println(getClass(), "", false, PRINT_DEBUG);
        Log.println(getClass(), "================== [ NEW MAP INIT ] ===================", false, PRINT_DEBUG);
        Log.println(getClass(), "1. Switching to map: " + packetData.gameMap, false, PRINT_DEBUG);

        Log.println(InitializeGameMap.class, "2. Switching to map: " + packetData.gameMap, false, PRINT_DEBUG);
        EntityManager.getInstance().setPlayerClient(null);
        EntityManager.getInstance().getEntities().clear();
        Valenguard.gameScreen.getMapRenderer().setTiledMap(packetData.gameMap);
    }

    @AllArgsConstructor
    class InitGameMapPacket extends PacketData {
        private String gameMap;
    }
}
