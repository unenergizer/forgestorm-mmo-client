package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.network.game.shared.Opcodes;

public class PingPacketOut extends AbstractClientOutPacket {

    public PingPacketOut() {
        super(Opcodes.PING);
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        //Nothing to write. Sending opcode only.
    }
}
