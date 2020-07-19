package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.network.game.shared.Opcodes;

public class AbilityRequestPacketOut extends AbstractClientPacketOut {

    private final short abilityId;
    private final EntityType entityType;
    private final short serverEntityID;

    public AbilityRequestPacketOut(short abilityId, MovingEntity movingEntity) {
        super(Opcodes.ABILITY_REQUEST);
        this.abilityId = abilityId;
        this.entityType = movingEntity.getEntityType();
        this.serverEntityID = movingEntity.getServerEntityID();
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        write.writeShort(abilityId);
        write.writeByte(entityType.getEntityTypeByte());
        write.writeShort(serverEntityID);
    }
}
