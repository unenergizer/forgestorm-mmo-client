package com.valenguard.client.network.packet.in;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.screens.ui.actors.event.StatsUpdateEvent;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ATTRIBUTES_UPDATE)
public class EntityAttributesUpdatePacketIn implements PacketListener<EntityAttributesUpdatePacketIn.EntityAttributesUpdatePacket> {

    private final static boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final int armor = clientHandler.readInt();
        final int damage = clientHandler.readInt();

        Attributes attributesUpdate = new Attributes();
        attributesUpdate.setArmor(armor);
        attributesUpdate.setDamage(damage);

        return new EntityAttributesUpdatePacket(entityId, attributesUpdate);
    }

    @Override
    public void onEvent(EntityAttributesUpdatePacket packetData) {
        println(getClass(), "ID: " + packetData.entityId, false, PRINT_DEBUG);
        println(getClass(), "Armor: " + packetData.attributes.getArmor(), false, PRINT_DEBUG);
        println(getClass(), "Damage: " + packetData.attributes.getDamage(), false, PRINT_DEBUG);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        if (playerClient != null && packetData.entityId == playerClient.getServerEntityID()) {
            // Update PlayerClient live attributes
            Attributes attributes = playerClient.getAttributes();
            attributes.setArmor(packetData.attributes.getArmor());
            attributes.setDamage(packetData.attributes.getDamage());

            // Update UI values
            StatsUpdateEvent statsUpdateEvent = new StatsUpdateEvent(attributes);
            for (Actor actor : Valenguard.getInstance().getStageHandler().getStage().getActors()) {
                actor.fire(statsUpdateEvent);
            }

            println(getClass(), "Updated player client attributes", false, PRINT_DEBUG);
        } else if (EntityManager.getInstance().getMovingEntity(packetData.entityId) != null) {
            MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
            Attributes attributes = movingEntity.getAttributes();
            attributes.setArmor(packetData.attributes.getArmor());
            attributes.setDamage(packetData.attributes.getDamage());

            println(getClass(), "Updated moving entity attributes", false, PRINT_DEBUG);
        } else {
            println(getClass(), "No attributes updated??", true);
        }
    }

    @AllArgsConstructor
    class EntityAttributesUpdatePacket extends PacketData {
        private final short entityId;
        private final Attributes attributes;
    }
}
