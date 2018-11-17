package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.entities.Entity;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.APPEARANCE)
public class EntityAppearenceChange implements PacketListener<EntityAppearenceChange.EntityAppearencePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new EntityAppearencePacket(
                clientHandler.readShort(),
                new short[]{clientHandler.readShort(), clientHandler.readShort()}
        );
    }

    @Override
    public void onEvent(EntityAppearencePacket packetData) {
        Entity entity = EntityManager.getInstance().getEntity(packetData.entityId);
        MovingEntity movingEntity = (MovingEntity) entity; // TODO generalize
        movingEntity.setBodyParts(packetData.textureIds[0], packetData.textureIds[1]);
    }

    @AllArgsConstructor
    class EntityAppearencePacket extends PacketData {
        private final short entityId;
        private final short[] textureIds;
    }
}
