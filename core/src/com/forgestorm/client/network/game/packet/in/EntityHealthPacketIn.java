package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_HEAL_OUT)
public class EntityHealthPacketIn implements PacketListener<EntityHealthPacketIn.PlayerHealthPacket> {

    private static final boolean PRINT_DEBUG = false;

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
                ClientMain.getInstance().getStageHandler().getStatusBar().updateHealth(movingEntity.getCurrentHealth() + packetData.healthGiven);
                break;
            case PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = EntityManager.getInstance().getAiEntity(packetData.entityId);
                break;
        }

        println(getClass(), "[" + movingEntity.getEntityName() + "] Health Before: " + movingEntity.getCurrentHealth(), false, PRINT_DEBUG);
        movingEntity.setCurrentHealth(movingEntity.getCurrentHealth() + packetData.healthGiven);
        println(getClass(), "[" + movingEntity.getEntityName() + "] Health After: " + movingEntity.getCurrentHealth(), false, PRINT_DEBUG);

        ClientMain.getInstance().getStageHandler().getTargetStatusBar().updateHealth(movingEntity);
    }

    @AllArgsConstructor
    class PlayerHealthPacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final int healthGiven;
    }
}
