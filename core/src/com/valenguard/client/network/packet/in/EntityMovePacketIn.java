package com.valenguard.client.network.packet.in;


import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.movement.MoveUtil;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ENTITY_MOVE_UPDATE)
public class EntityMovePacketIn implements PacketListener<EntityMovePacketIn.EntityMovePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final short futureX = clientHandler.readShort();
        final short futureY = clientHandler.readShort();

        return new EntityMovePacket(entityId, EntityType.getEntityType(entityType), futureX, futureY);
    }

    @Override
    public void onEvent(EntityMovePacket packetData) {
        MovingEntity movingEntity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
                Location resyncLocation = new Location(playerClient.getMapName(), packetData.futureX, packetData.futureY);

                playerClient.setCurrentMapLocation(resyncLocation);
                playerClient.setFutureMapLocation(resyncLocation);
                playerClient.setDrawX(resyncLocation.getX() * ClientConstants.TILE_SIZE);
                playerClient.setDrawY(resyncLocation.getY() * ClientConstants.TILE_SIZE);

                Valenguard.getInstance().getClientMovementProcessor().resetInput();
                return;
            case PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
                break;
        }

        if (MoveUtil.isEntityMoving(movingEntity)) {
            movingEntity.addLocationToFutureQueue(new Location(movingEntity.getMapName(), packetData.futureX, packetData.futureY));
        } else {
            Valenguard.getInstance().getEntityMovementManager().updateEntityFutureLocation(movingEntity, new Location(movingEntity.getMapName(), packetData.futureX, packetData.futureY));
        }
    }

    @AllArgsConstructor
    class EntityMovePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final short futureX;
        private final short futureY;
    }
}
