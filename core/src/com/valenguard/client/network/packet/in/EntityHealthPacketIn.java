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

@Opcode(getOpcode = Opcodes.ENTITY_HEAL_OUT)
public class EntityHealthPacketIn implements PacketListener<EntityHealthPacketIn.PlayerTeleportPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final int healthGiven = clientHandler.readInt();

        return new PlayerTeleportPacket(entityId, healthGiven);
    }

    @Override
    public void onEvent(PlayerTeleportPacket packetData) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        if (playerClient != null && packetData.entityId == playerClient.getServerEntityID()) {
            // Player damageTake and currentHealth indicator

            playerClient.setCurrentHealth(playerClient.getCurrentHealth() + packetData.healthGiven);
            println(getClass(), "PlayerClient ID: " + packetData.entityId + ", HP GIVEN: " + packetData.healthGiven);

        } else if (EntityManager.getInstance().getMovingEntity(packetData.entityId) != null) {
            // MovingEntity damageTake and currentHealth indicator
            MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
            movingEntity.setCurrentHealth(movingEntity.getCurrentHealth() + packetData.healthGiven);

            println(getClass(), "MovingEntity ID: " + packetData.entityId + ", HP GIVEN: " + packetData.healthGiven);
        } else {
            println(getClass(), "Something should have teleported??");
        }
    }

    @AllArgsConstructor
    class PlayerTeleportPacket extends PacketData {
        private final short entityId;
        private final int healthGiven;
    }
}
