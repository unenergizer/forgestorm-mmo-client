package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class AppearanceChange extends ClientOutPacket {

    private final short headId, bodyId;

    public AppearanceChange(short headId, short bodyId) {
        super(Opcodes.APPEARANCE);
        this.headId = headId;
        this.bodyId = bodyId;
    }

    @Override
    protected void createPacket(DataOutputStream write) throws IOException {
        write.writeShort(headId);
        write.writeShort(bodyId);
    }
}
