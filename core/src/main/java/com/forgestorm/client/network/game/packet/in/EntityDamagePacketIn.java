package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_DAMAGE_OUT)
public class EntityDamagePacketIn implements PacketListener<EntityDamagePacketIn.EntityDamagePacket> {

    private final static boolean PRINT_DEBUG = false;

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
                ClientMain.getInstance().getStageHandler().getStatusBar().updateHealth(packetData.health);
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
        println(getClass(), "[" + movingEntity.getEntityName() + "] Health Before: " + movingEntity.getCurrentHealth(), false, PRINT_DEBUG);
        movingEntity.setCurrentHealth(packetData.health);
        println(getClass(), "[" + movingEntity.getEntityName() + "] Health After: " + movingEntity.getCurrentHealth(), false, PRINT_DEBUG);

        ClientMain.getInstance().getStageHandler().getTargetStatusBar().updateHealth(movingEntity);
    }

    @AllArgsConstructor
    class EntityDamagePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final int health;
        private final int damageTaken;
    }
}
