package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_DESPAWN)
public class EntityDespawnPacketIn implements PacketListener<EntityDespawnPacketIn.EntityDespawnPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityUUID = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();

        println(getClass(), "###[ DESPAWN IN ]################################", false, PRINT_DEBUG);
        println(getClass(), "EntityID: " + entityUUID, false, PRINT_DEBUG);
        println(getClass(), "EntityType: " + EntityType.getEntityType(entityType), false, PRINT_DEBUG);

        return new EntityDespawnPacket(entityUUID, EntityType.getEntityType(entityType));
    }

    @Override
    public void onEvent(EntityDespawnPacket packetData) {
        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                println(getClass(), "Tried to despawn CLIENT_PLAYER type!", true);
                break;
            case PLAYER:
                EntityManager.getInstance().removePlayerEntity(packetData.entityId);
                break;
            case MONSTER:
            case NPC:
                EntityManager.getInstance().removeAiEntity(packetData.entityId);
                break;
            case ITEM_STACK:
                EntityManager.getInstance().removeItemStackDrop(packetData.entityId);
                break;
            case SKILL_NODE:
                EntityManager.getInstance().removeStationaryEntity(packetData.entityId);
                break;
        }
    }

    @AllArgsConstructor
    class EntityDespawnPacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
    }
}
