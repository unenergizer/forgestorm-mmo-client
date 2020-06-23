package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.network.game.shared.Opcodes;

public class PingPacketOut extends AbstractClientPacketOut {

    public PingPacketOut() {
        super(Opcodes.PING);
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        //Nothing to write. Sending opcode only.
    }
}
