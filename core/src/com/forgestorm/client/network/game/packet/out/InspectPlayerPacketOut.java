package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.network.game.shared.Opcodes;

public class InspectPlayerPacketOut extends AbstractClientPacketOut {

    private final short serverEntityID;

    public InspectPlayerPacketOut(short serverEntityID) {
        super(Opcodes.INSPECT_PLAYER);
        this.serverEntityID = serverEntityID;
    }

    @Override
    void createPacket(ForgeStormOutputStream write) {
        write.writeShort(serverEntityID);
    }
}
