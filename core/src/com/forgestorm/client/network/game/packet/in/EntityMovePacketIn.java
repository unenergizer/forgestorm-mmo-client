package com.forgestorm.client.network.game.packet.in;


import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.movement.MoveUtil;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

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

                ClientMain.getInstance().getClientMovementProcessor().resetInput();
                return;
            case PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = EntityManager.getInstance().getAiEntity(packetData.entityId);
                break;
        }

        if (movingEntity == null) {
//            throw new RuntimeException("Server tried to move an entity our client is not aware of. Check to make sure the server were on the correct map.");
            println(getClass(), "Server tried to move an entity our client is not aware of. Check to make sure the server were on the correct map.", true, true);
            return;
        }

        if (MoveUtil.isEntityMoving(movingEntity)) {
            movingEntity.addLocationToFutureQueue(new Location(movingEntity.getMapName(), packetData.futureX, packetData.futureY));
        } else {
            ClientMain.getInstance().getEntityMovementManager().updateEntityFutureLocation(movingEntity, new Location(movingEntity.getMapName(), packetData.futureX, packetData.futureY));
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