package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;

public class PingPacketOut extends ClientAbstractOutPacket {

    public PingPacketOut() {
        super(Opcodes.PING);
    }

    @Override
    protected void createPacket(DataOutputStream write) {
        //Nothing to write. Sending opcode only.
    }
}
