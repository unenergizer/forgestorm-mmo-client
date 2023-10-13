package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class InspectPlayerPacketOut extends AbstractPacketOut {

    private final short serverEntityID;

    public InspectPlayerPacketOut(ClientMain clientMain, short serverEntityID) {
        super(clientMain, Opcodes.INSPECT_PLAYER);
        this.serverEntityID = serverEntityID;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeShort(serverEntityID);
    }
}
