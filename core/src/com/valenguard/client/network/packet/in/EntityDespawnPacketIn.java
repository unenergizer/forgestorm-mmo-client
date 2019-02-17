package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ENTITY_DESPAWN)
public class EntityDespawnPacketIn implements PacketListener<EntityDespawnPacketIn.EntityDespawnPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new EntityDespawnPacket(clientHandler.readShort());
    }

    @Override
    public void onEvent(EntityDespawnPacket packetData) {
        EntityManager.getInstance().removeMovingEntity(packetData.entityId);
        EntityManager.getInstance().removeStationaryEntity(packetData.entityId);
        EntityManager.getInstance().removeItemStackDrop(packetData.entityId);
    }

    @AllArgsConstructor
    class EntityDespawnPacket extends PacketData {
        private final short entityId;
    }
}
