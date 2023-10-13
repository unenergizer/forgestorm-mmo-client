package com.forgestorm.client.network.game.packet.in;


import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.movement.ClientPlayerMovementManager;
import com.forgestorm.client.game.movement.MoveUtil;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.client.util.MoveNode;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;
import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.ENTITY_MOVE_UPDATE)
public class EntityMovePacketIn implements PacketListener<EntityMovePacketIn.EntityMovePacket> {

    private final ClientMain clientMain;

    public EntityMovePacketIn(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final int futureX = clientHandler.readInt();
        final int futureY = clientHandler.readInt();
        final short futureZ = clientHandler.readShort();

        return new EntityMovePacket(entityId, EntityType.getEntityType(entityType), futureX, futureY, futureZ);
    }

    @Override
    public void onEvent(EntityMovePacket packetData) {
        MovingEntity movingEntity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:

                ClientPlayerMovementManager movementManager = clientMain.getClientPlayerMovementManager();

                MoveNode moveNode = movementManager.getMovesSentToServer().poll();
                if (moveNode == null) {
                    println(getClass(), "The response for the client's movement was received but no move node was not the move queue.");
                    return;
                }

                if (moveNode.getWorldX() != packetData.futureX || moveNode.getWorldY() != packetData.futureY) {
                    println(getClass(), "The response for the client's movement did not match what was expected.");
                }

                return;
            case PLAYER:
                movingEntity = clientMain.getEntityManager().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = clientMain.getEntityManager().getAiEntity(packetData.entityId);
                break;
        }

        if (movingEntity == null) {
//            throw new RuntimeException("Server tried to move an entity our client is not aware of. Check to make sure the server were on the correct map.");
            println(getClass(), "Server tried to move an entity our client is not aware of. Check to make sure the server were on the correct map.", true, true);
            return;
        }

        if (MoveUtil.isEntityMoving(movingEntity)) {
            movingEntity.addLocationToFutureQueue(new Location(clientMain, movingEntity.getWorldName(), packetData.futureX, packetData.futureY, packetData.futureZ));
        } else {
            clientMain.getEntityMovementManager().updateEntityFutureLocation(movingEntity, new Location(clientMain, movingEntity.getWorldName(), packetData.futureX, packetData.futureY, packetData.futureZ));
        }
    }

    @AllArgsConstructor
    static class EntityMovePacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final int futureX;
        private final int futureY;
        private final short futureZ;
    }
}
