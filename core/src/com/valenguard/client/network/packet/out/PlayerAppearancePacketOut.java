package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerAppearancePacketOut extends ClientAbstractOutPacket {

    private final short headId, bodyId;

    public PlayerAppearancePacketOut(short headId, short bodyId) {
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
