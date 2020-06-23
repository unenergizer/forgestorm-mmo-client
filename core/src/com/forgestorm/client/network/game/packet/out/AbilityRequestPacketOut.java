package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.network.game.shared.Opcodes;

public class AbilityRequestPacketOut extends AbstractClientPacketOut {

    private final short abilityId;
    private final MovingEntity movingEntity;

    public AbilityRequestPacketOut(short abilityId, MovingEntity movingEntity) {
        super(Opcodes.ABILITY_REQUEST);
        this.abilityId = abilityId;
        this.movingEntity = movingEntity;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        write.writeShort(abilityId);
        write.writeByte(movingEntity.getEntityType().getEntityTypeByte());
        write.writeShort(movingEntity.getServerEntityID());
    }
}
