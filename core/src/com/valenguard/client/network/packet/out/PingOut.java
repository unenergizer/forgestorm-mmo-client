package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;

import java.io.ObjectOutputStream;

public class PingOut extends ClientOutPacket {

    public PingOut() {
        super(Opcodes.PING);
    }

    @Override
    protected void createPacket(ObjectOutputStream write) {
        //Nothing to write. Sending opcode only.
    }
}
