package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
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
public class EntityHealthPacketIn implements PacketListener<EntityHealthPacketIn.PlayerHealthPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final int healthGiven = clientHandler.readInt();

        return new PlayerHealthPacket(entityId, healthGiven);
    }

    @Override
    public void onEvent(PlayerHealthPacket packetData) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        if (playerClient != null && packetData.entityId == playerClient.getServerEntityID()) {
            // Player current health indicator
            int health = playerClient.getCurrentHealth() + packetData.healthGiven;
            playerClient.setCurrentHealth(health);
            Valenguard.getInstance().getStageHandler().getStatusBar().updateHealth(health);
            println(getClass(), "HP IN: " + health);

        } else if (EntityManager.getInstance().getMovingEntity(packetData.entityId) != null) {
            // MovingEntity current health indicator
            MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
            movingEntity.setCurrentHealth(movingEntity.getCurrentHealth() + packetData.healthGiven);
        }
    }

    @AllArgsConstructor
    class PlayerHealthPacket extends PacketData {
        private final short entityId;
        private final int healthGiven;
    }
}
