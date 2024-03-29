package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ENTITY_UPDATE_SPEED)
public class EntityUpdatePacketIn implements PacketListener<EntityUpdatePacketIn.EntitySpeedPacket> {

    private final ClientMain clientMain;

    public EntityUpdatePacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityUUID = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final float runSpeed = clientHandler.readFloat();

        return new EntitySpeedPacket(entityUUID, EntityType.getEntityType(entityType), runSpeed);
    }

    @Override
    public void onEvent(EntitySpeedPacket packetData) {
        EntityManager entityManager = clientMain.getEntityManager();
        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                entityManager.getPlayerClient().setMoveSpeed(packetData.runSpeed);
                break;
            case PLAYER:
                entityManager.getPlayerEntity(packetData.entityUUID).setMoveSpeed(packetData.runSpeed);
                break;
            case NPC:
            case MONSTER:
                entityManager.getAiEntity(packetData.entityUUID).setMoveSpeed(packetData.runSpeed);
                break;
        }
    }

    @AllArgsConstructor
    static class EntitySpeedPacket extends PacketData {
        private final short entityUUID;
        private final EntityType entityType;
        private final float runSpeed;
    }
}
