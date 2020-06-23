package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.network.game.shared.Opcodes;

class PlayerAppearancePacketOut extends AbstractClientPacketOut {

    private final short headId, bodyId;

    public PlayerAppearancePacketOut(short headId, short bodyId) {
        super(Opcodes.APPEARANCE);
        this.headId = headId;
        this.bodyId = bodyId;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeShort(headId);
        write.writeShort(bodyId);
    }
}
