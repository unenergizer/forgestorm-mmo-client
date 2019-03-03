package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_DESPAWN)
public class EntityDespawnPacketIn implements PacketListener<EntityDespawnPacketIn.EntityDespawnPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityUUID = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        return new EntityDespawnPacket(entityUUID, EntityType.getEntityType(entityType));
    }

    @Override
    public void onEvent(EntityDespawnPacket packetData) {
        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                println(getClass(), "Tried to despawn CLIENT_PLAYER type!", true);
                break;
            case PLAYER:
            case MONSTER:
            case NPC:
                EntityManager.getInstance().removeMovingEntity(packetData.entityId);
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
