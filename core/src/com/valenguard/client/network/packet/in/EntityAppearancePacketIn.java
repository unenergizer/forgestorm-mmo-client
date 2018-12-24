package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.assets.GameAtlas;
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
public class EntityAppearancePacketIn implements PacketListener<EntityAppearancePacketIn.EntityAppearancePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new EntityAppearancePacket(
                clientHandler.readShort(),
                new short[]{clientHandler.readShort(), clientHandler.readShort()}
        );
    }

    @Override
    public void onEvent(EntityAppearancePacket packetData) {
        Entity entity = EntityManager.getInstance().getEntity(packetData.entityId);
        MovingEntity movingEntity = (MovingEntity) entity; // TODO generalize
        movingEntity.loadTextures(GameAtlas.ENTITY_CHARACTER, packetData.textureIds);
    }

    @AllArgsConstructor
    class EntityAppearancePacket extends PacketData {
        private final short entityId;
        private final short[] textureIds;
    }
}
