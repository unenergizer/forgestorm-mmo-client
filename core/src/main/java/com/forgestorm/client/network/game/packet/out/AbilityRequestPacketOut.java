package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

public class AbilityRequestPacketOut extends AbstractPacketOut {

    private final short abilityId;
    private final EntityType entityType;
    private final short serverEntityID;

    public AbilityRequestPacketOut(ClientMain clientMain, short abilityId, MovingEntity movingEntity) {
        super(clientMain, Opcodes.ABILITY_REQUEST);
        this.abilityId = abilityId;
        this.entityType = movingEntity.getEntityType();
        this.serverEntityID = movingEntity.getServerEntityID();
    }

    @Override
    public void createPacket(GameOutputStream write) {
        write.writeShort(abilityId);
        write.writeByte(entityType.getEntityTypeByte());
        write.writeShort(serverEntityID);
    }
}
