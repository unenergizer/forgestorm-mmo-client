package com.forgestorm.client.network.game.packet.in;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.event.StatsUpdateEvent;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.game.rpg.Attributes;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ATTRIBUTES_UPDATE)
public class EntityAttributesUpdatePacketIn implements PacketListener<EntityAttributesUpdatePacketIn.EntityAttributesUpdatePacket> {

    private static final boolean PRINT_DEBUG = false;
    private final ClientMain clientMain;

    public EntityAttributesUpdatePacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final EntityType entityType = EntityType.getEntityType(clientHandler.readByte());
        final int armor = clientHandler.readInt();
        final int damage = clientHandler.readInt();

        Attributes attributesUpdate = new Attributes();
        attributesUpdate.setArmor(armor);
        attributesUpdate.setDamage(damage);

        println(getClass(), "EntityId: " + entityId, false, PRINT_DEBUG);
        println(getClass(), "EntityType: " + entityType, false, PRINT_DEBUG);
        println(getClass(), "Armor: " + armor, false, PRINT_DEBUG);
        println(getClass(), "Damage: " + damage, false, PRINT_DEBUG);

        return new EntityAttributesUpdatePacket(entityId, entityType, attributesUpdate);
    }

    @Override
    public void onEvent(EntityAttributesUpdatePacket packetData) {
        MovingEntity movingEntity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                movingEntity = clientMain.getEntityManager().getPlayerClient();
                break;
            case PLAYER:
                movingEntity = clientMain.getEntityManager().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = clientMain.getEntityManager().getAiEntity(packetData.entityId);
                break;
        }

        Attributes attributes = movingEntity.getAttributes();
        attributes.setArmor(packetData.attributes.getArmor());
        attributes.setDamage(packetData.attributes.getDamage());

        println(getClass(), "Updating UI elements for attributes..", false, PRINT_DEBUG);

        // Update UI values
        if (packetData.entityType == EntityType.CLIENT_PLAYER) {
            StatsUpdateEvent statsUpdateEvent = new StatsUpdateEvent(attributes);
            for (Actor actor : clientMain.getStageHandler().getStage().getActors()) {
                actor.fire(statsUpdateEvent);
            }
        }
    }

    @AllArgsConstructor
    static class EntityAttributesUpdatePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final Attributes attributes;
    }
}
