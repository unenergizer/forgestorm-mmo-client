package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class InspectPlayerPacketOut extends AbstractPacketOut {

    private final short serverEntityID;

    public InspectPlayerPacketOut(short serverEntityID) {
        super(Opcodes.INSPECT_PLAYER);
        this.serverEntityID = serverEntityID;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeShort(serverEntityID);
    }
}
