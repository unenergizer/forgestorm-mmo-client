package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class ProfileRequestPacketOut extends AbstractPacketOut {

    private final short serverEntityID;

    public ProfileRequestPacketOut(ClientMain clientMain, short serverEntityID) {
        super(clientMain, Opcodes.PROFILE_REQUEST);
        this.serverEntityID = serverEntityID;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeShort(serverEntityID);
    }
}
