package com.valenguard.client.network.game.packet.in;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.event.StatsUpdateEvent;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ATTRIBUTES_UPDATE)
public class EntityAttributesUpdatePacketIn implements PacketListener<EntityAttributesUpdatePacketIn.EntityAttributesUpdatePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final int armor = clientHandler.readInt();
        final int damage = clientHandler.readInt();

        Attributes attributesUpdate = new Attributes();
        attributesUpdate.setArmor(armor);
        attributesUpdate.setDamage(damage);

        return new EntityAttributesUpdatePacket(entityId, EntityType.getEntityType(entityType), attributesUpdate);
    }

    @Override
    public void onEvent(EntityAttributesUpdatePacket packetData) {
        MovingEntity movingEntity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerClient();
                break;
            case PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = EntityManager.getInstance().getAiEntity(packetData.entityId);
                break;
        }

        Attributes attributes = movingEntity.getAttributes();
        attributes.setArmor(packetData.attributes.getArmor());
        attributes.setDamage(packetData.attributes.getDamage());

        // Update UI values
        if (packetData.entityType == EntityType.CLIENT_PLAYER) {
            StatsUpdateEvent statsUpdateEvent = new StatsUpdateEvent(attributes);
            for (Actor actor : ActorUtil.getStage().getActors()) {
                actor.fire(statsUpdateEvent);
            }
        }
    }

    @AllArgsConstructor
    class EntityAttributesUpdatePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final Attributes attributes;
    }
}
