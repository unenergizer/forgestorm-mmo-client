package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.Player;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_DAMAGE_OUT)
public class EntityDamagePacketIn implements PacketListener<EntityDamagePacketIn.EntityDamagePacket> {

    private final static boolean PRINT_DEBUG = true;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final int health = clientHandler.readInt();
        final int damageTaken = clientHandler.readInt();

        return new EntityDamagePacket(entityId, EntityType.getEntityType(entityType), health, damageTaken);
    }

    @Override
    public void onEvent(EntityDamagePacket packetData) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        switch (packetData.entityType) {

            case CLIENT_PLAYER:
                // Player damageTake and currentHealth indicator
                playerClient.setDamageTaken(playerClient.getDamageTaken() + packetData.damageTaken);
                playerClient.setShowDamage(true);

                playerClient.setCurrentHealth(packetData.health);
                Valenguard.getInstance().getStageHandler().getStatusBar().updateHealth(packetData.health);

                println(getClass(), "PlayerClient ID: " + packetData.entityId + ", HP: " + packetData.health + ", DMG: " + packetData.damageTaken, false, PRINT_DEBUG);

                break;
            case PLAYER:
                // MovingEntity damageTake and currentHealth indicator
                Player playerEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                playerEntity.setDamageTaken(packetData.damageTaken);
                playerEntity.setShowDamage(true);
                playerEntity.setCurrentHealth(packetData.health);

                println(getClass(), "MovingEntity ID: " + packetData.entityId + ", HP: " + packetData.health + ", DMG: " + packetData.damageTaken, false, PRINT_DEBUG);
                break;
            case NPC:
            case MONSTER:
                // MovingEntity damageTake and currentHealth indicator
                MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
                movingEntity.setDamageTaken(packetData.damageTaken);
                movingEntity.setShowDamage(true);
                movingEntity.setCurrentHealth(packetData.health);

                println(getClass(), "MovingEntity ID: " + packetData.entityId + ", HP: " + packetData.health + ", DMG: " + packetData.damageTaken, false, PRINT_DEBUG);
                break;
        }

        if (playerClient != null && packetData.entityId == playerClient.getServerEntityID()) {
            // Player damageTake and currentHealth indicator
            playerClient.setDamageTaken(playerClient.getDamageTaken() + packetData.damageTaken);
            playerClient.setShowDamage(true);

            playerClient.setCurrentHealth(packetData.health);
            Valenguard.getInstance().getStageHandler().getStatusBar().updateHealth(packetData.health);

            println(getClass(), "PlayerClient ID: " + packetData.entityId + ", HP: " + packetData.health + ", DMG: " + packetData.damageTaken, false, PRINT_DEBUG);

        } else if (EntityManager.getInstance().getMovingEntity(packetData.entityId) != null) {
            // MovingEntity damageTake and currentHealth indicator
            MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
            movingEntity.setDamageTaken(packetData.damageTaken);
            movingEntity.setShowDamage(true);
            movingEntity.setCurrentHealth(packetData.health);

            println(getClass(), "MovingEntity ID: " + packetData.entityId + ", HP: " + packetData.health + ", DMG: " + packetData.damageTaken, false, PRINT_DEBUG);
        }
    }

    @AllArgsConstructor
    class EntityDamagePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final int health;
        private final int damageTaken;
    }
}
