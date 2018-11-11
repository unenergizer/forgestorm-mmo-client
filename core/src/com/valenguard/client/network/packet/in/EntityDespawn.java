package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ENTITY_DESPAWN)
public class EntityDespawn implements PacketListener<EntityDespawn.EntityDespawnPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new EntityDespawnPacket(clientHandler.readShort());
    }

    @Override
    public void onEvent(EntityDespawnPacket packetData) {
        EntityManager.getInstance().removeEntity(packetData.entityId);
    }

    @AllArgsConstructor
    class EntityDespawnPacket extends PacketData {
        private final short entityId;
    }
}
