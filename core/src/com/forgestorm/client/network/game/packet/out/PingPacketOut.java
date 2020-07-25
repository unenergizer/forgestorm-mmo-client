package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcodes;

public class PingPacketOut extends AbstractClientPacketOut {

    private ClientHandler clientHandler;

    public PingPacketOut(ClientHandler clientHandler) {
        super(Opcodes.PING);
        this.clientHandler = clientHandler;
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        long pingSentTime = System.currentTimeMillis();
        clientHandler.setPingSendTime(pingSentTime);
    }
}
