package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

class PlayerAppearancePacketOut extends AbstractPacketOut {

    private final short headId, bodyId;

    public PlayerAppearancePacketOut(ClientMain clientMain, short headId, short bodyId) {
        super(clientMain, Opcodes.APPEARANCE);
        this.headId = headId;
        this.bodyId = bodyId;
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeShort(headId);
        write.writeShort(bodyId);
    }
}
