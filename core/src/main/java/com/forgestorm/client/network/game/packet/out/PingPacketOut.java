package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class PingPacketOut extends AbstractPacketOut {

    private final ClientHandler clientHandler;

    public PingPacketOut(ClientMain clientMain, ClientHandler clientHandler) {
        super(clientMain, Opcodes.PING);
        this.clientHandler = clientHandler;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        long pingSentTime = System.currentTimeMillis();
        clientHandler.setPingSendTime(pingSentTime);
    }
}
