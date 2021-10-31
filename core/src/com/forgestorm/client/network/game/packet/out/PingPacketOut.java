package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class PingPacketOut extends AbstractPacketOut {

    private final ClientHandler clientHandler;

    public PingPacketOut(ClientHandler clientHandler) {
        super(Opcodes.PING);
        this.clientHandler = clientHandler;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        long pingSentTime = System.currentTimeMillis();
        clientHandler.setPingSendTime(pingSentTime);
    }
}
