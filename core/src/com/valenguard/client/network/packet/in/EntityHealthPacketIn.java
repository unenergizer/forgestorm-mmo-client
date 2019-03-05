package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ENTITY_HEAL_OUT)
public class EntityHealthPacketIn implements PacketListener<EntityHealthPacketIn.PlayerHealthPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final int healthGiven = clientHandler.readInt();

        return new PlayerHealthPacket(entityId, EntityType.getEntityType(entityType), healthGiven);
    }

    @Override
    public void onEvent(PlayerHealthPacket packetData) {
        MovingEntity movingEntity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerClient();
                Valenguard.getInstance().getStageHandler().getStatusBar().updateHealth(movingEntity.getCurrentHealth() + packetData.healthGiven);
                break;
            case PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
                break;
        }

        movingEntity.setCurrentHealth(movingEntity.getCurrentHealth() + packetData.healthGiven);
    }

    @AllArgsConstructor
    class PlayerHealthPacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final int healthGiven;
    }
}
