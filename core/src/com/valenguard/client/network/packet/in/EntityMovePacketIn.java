package com.valenguard.client.network.packet.in;


import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
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

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_MOVE_UPDATE)
public class EntityMovePacketIn implements PacketListener<EntityMovePacketIn.EntityMovePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new EntityMovePacket(clientHandler.readShort(), clientHandler.readShort(), clientHandler.readShort());
    }

    @Override
    public void onEvent(EntityMovePacket packetData) {
        MovingEntity entity = EntityManager.getInstance().getMovingEntity(packetData.entityId);

        if (entity == null) {
            if (EntityManager.getInstance().getPlayerClient() == null) return;
            if (EntityManager.getInstance().getPlayerClient().getServerEntityID() == packetData.entityId) {
                PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

                Location resyncLocation = new Location(playerClient.getMapName(), packetData.futureX, packetData.futureY);

                playerClient.setCurrentMapLocation(resyncLocation);
                playerClient.setFutureMapLocation(resyncLocation);

                playerClient.setDrawX(resyncLocation.getX() * ClientConstants.TILE_SIZE);
                playerClient.setDrawY(resyncLocation.getY() * ClientConstants.TILE_SIZE);
                Valenguard.getInstance().getClientMovementProcessor().resetInput();


            } else {
                println(getClass(), "Tried to move null entity. ID: " + packetData.entityId, true);
            }
            return;
        }

        if (MoveUtil.isEntityMoving(entity)) {
            entity.addLocationToFutureQueue(new Location(entity.getMapName(), packetData.futureX, packetData.futureY));
        } else {
            Valenguard.getInstance().getEntityMovementManager().updateEntityFutureLocation(entity, new Location(entity.getMapName(), packetData.futureX, packetData.futureY));
        }
    }

    @AllArgsConstructor
    class EntityMovePacket extends PacketData {
        private final short entityId;
        private final short futureX;
        private final short futureY;
    }
}
