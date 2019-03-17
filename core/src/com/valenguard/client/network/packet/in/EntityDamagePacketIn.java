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

@Opcode(getOpcode = Opcodes.ENTITY_DAMAGE_OUT)
public class EntityDamagePacketIn implements PacketListener<EntityDamagePacketIn.EntityDamagePacket> {

    private final static boolean PRINT_DEBUG = true;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final int health = clientHandler.readInt();
        final int damageTaken = clientHandler.readInt();

        return new EntityDamagePacket(entityId, EntityType.getEntityType(entityType), health, damageTaken);
    }

    @Override
    public void onEvent(EntityDamagePacket packetData) {
        MovingEntity movingEntity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerClient();
                movingEntity.setDamageTaken(movingEntity.getDamageTaken() + packetData.damageTaken);
                Valenguard.getInstance().getStageHandler().getStatusBar().updateHealth(packetData.health);
                break;
            case PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                movingEntity.setDamageTaken(packetData.damageTaken);
                break;
            case NPC:
            case MONSTER:
                movingEntity = EntityManager.getInstance().getAiEntity(packetData.entityId);
                movingEntity.setDamageTaken(packetData.damageTaken);
                break;
        }

        movingEntity.setShowDamage(true);
        movingEntity.setCurrentHealth(packetData.health);
    }

    @AllArgsConstructor
    class EntityDamagePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final int health;
        private final int damageTaken;
    }
}
