package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.INIT_WORLD)
public class InitializeGameWorldPacketIn implements PacketListener<InitializeGameWorldPacketIn.InitGameMapPacket> {

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public InitializeGameWorldPacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new InitGameMapPacket(clientHandler.readString());
    }

    @Override
    public void onEvent(InitGameMapPacket packetData) {
        println(getClass(), "", false, PRINT_DEBUG);
        println(getClass(), "================== [ NEW WORLD INIT ] ===================", false, PRINT_DEBUG);
        println(getClass(), "1. Switching to map: " + packetData.gameMap, false, PRINT_DEBUG);

        clientMain.getEntityManager().dispose(); // quick clear existing entities
        clientMain.getWorldManager().setGameWorld(packetData.gameMap);
        clientMain.getClientMovementProcessor().resetInput();
        clientMain.getStageHandler().getTargetStatusBar().hideTargetStatusBar(null);
    }

    @AllArgsConstructor
    static class InitGameMapPacket extends PacketData {
        private final String gameMap;
    }
}
