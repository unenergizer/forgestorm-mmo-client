package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;

public class PingPacketOut extends AbstractClientOutPacket {

    public PingPacketOut() {
        super(Opcodes.PING);
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        //Nothing to write. Sending opcode only.
    }
}
