package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.network.game.shared.Opcodes;

public class ProfileRequestPacketOut extends AbstractClientPacketOut {

    private final short serverEntityID;

    public ProfileRequestPacketOut(short serverEntityID) {
        super(Opcodes.PROFILE_REQUEST);
        this.serverEntityID = serverEntityID;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeShort(serverEntityID);
    }
}
