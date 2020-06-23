package com.forgestorm.client.network.game.packet.in;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.game.rpg.Attributes;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.event.StatsUpdateEvent;
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

@Opcode(getOpcode = Opcodes.ATTRIBUTES_UPDATE)
public class EntityAttributesUpdatePacketIn implements PacketListener<EntityAttributesUpdatePacketIn.EntityAttributesUpdatePacket> {

    private static final boolean PRINT_DEBUG = false;

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

        println(getClass(), "Updating UI elements for attributes..", false, PRINT_DEBUG);

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
