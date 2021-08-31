package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.PLAYER_TELEPORT)
public class MovingEntityTeleportPacketIn implements PacketListener<MovingEntityTeleportPacketIn.MovingEntityTeleportPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final byte entityType = clientHandler.readByte();
        final String worldName = clientHandler.readString();
        final int x = clientHandler.readInt();
        final int y = clientHandler.readInt();
        final short worldZ = clientHandler.readShort();
        final byte facingDirection = clientHandler.readByte();

        println(getClass(), "World: " + worldName, false, PRINT_DEBUG);
        println(getClass(), "x: " + x, false, PRINT_DEBUG);
        println(getClass(), "y: " + y, false, PRINT_DEBUG);
        println(getClass(), "worldZ: " + worldZ, false, PRINT_DEBUG);

        return new MovingEntityTeleportPacket(entityId, EntityType.getEntityType(entityType), new Location(worldName, x, y, worldZ), MoveDirection.getDirection(facingDirection));
    }

    @Override
    public void onEvent(MovingEntityTeleportPacket packetData) {
        MovingEntity movingEntity = null;

        switch (packetData.entityType) {
            case CLIENT_PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerClient();
                ClientMain.getInstance().getClientMovementProcessor().resetInput();
                break;
            case PLAYER:
                movingEntity = EntityManager.getInstance().getPlayerEntity(packetData.entityId);
                break;
            case NPC:
            case MONSTER:
                movingEntity = EntityManager.getInstance().getAiEntity(packetData.entityId);
                break;
        }

        teleportMovingEntity(movingEntity, packetData.teleportLocation, packetData.facingDirection);
    }

    /**
     * Teleports a {@link MovingEntity} to the given {@link Location}.
     *
     * @param movingEntity    The {@link MovingEntity} we want to teleport.
     * @param location        The teleport destination.
     * @param facingDirection Which way the {@link MovingEntity} should be facing after teleporting.
     */
    private void teleportMovingEntity(MovingEntity movingEntity, Location location, MoveDirection facingDirection) {
        movingEntity.setCurrentMapLocation(location);
        movingEntity.setFutureMapLocation(location);
        movingEntity.setFacingDirection(facingDirection);
        movingEntity.setDrawX(location.getX() * ClientConstants.TILE_SIZE);
        movingEntity.setDrawY(location.getY() * ClientConstants.TILE_SIZE);
        movingEntity.getFutureLocationRequests().clear();
        movingEntity.setWalkTime(0);
    }

    @AllArgsConstructor
    class MovingEntityTeleportPacket extends PacketData {
        private final short entityId;
        private final EntityType entityType;
        private final Location teleportLocation;
        private final MoveDirection facingDirection;
    }
}
