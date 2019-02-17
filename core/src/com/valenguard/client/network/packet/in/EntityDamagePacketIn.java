package com.valenguard.client.network.packet.in;

import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_DAMAGE_OUT)
public class EntityDamagePacketIn implements PacketListener<EntityDamagePacketIn.PlayerTeleportPacket> {

    private final static boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final int health = clientHandler.readInt();
        final int damageTaken = clientHandler.readInt();

        return new PlayerTeleportPacket(entityId, health, damageTaken);
    }

    @Override
    public void onEvent(PlayerTeleportPacket packetData) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        if (playerClient != null && packetData.entityId == playerClient.getServerEntityID()) {
            // Player damageTake and currentHealth indicator

            playerClient.setDamageTaken(playerClient.getDamageTaken() + packetData.damageTaken);
            playerClient.setShowDamage(true);
            playerClient.setCurrentHealth(packetData.health);
            println(getClass(), "PlayerClient ID: " + packetData.entityId + ", HP: " + packetData.health + ", DMG: " + packetData.damageTaken, false, PRINT_DEBUG);

        } else if (EntityManager.getInstance().getMovingEntity(packetData.entityId) != null) {
            // MovingEntity damageTake and currentHealth indicator
            MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
            movingEntity.setDamageTaken(packetData.damageTaken);
            movingEntity.setShowDamage(true);
            movingEntity.setCurrentHealth(packetData.health);

            println(getClass(), "MovingEntity ID: " + packetData.entityId + ", HP: " + packetData.health + ", DMG: " + packetData.damageTaken, false, PRINT_DEBUG);
        } else {
            println(getClass(), "Something should have teleported??", true);
        }
    }

    @AllArgsConstructor
    class PlayerTeleportPacket extends PacketData {
        private final short entityId;
        private final int health;
        private final int damageTaken;
    }
}
