package com.valenguard.client.network.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.PLAYER_TELEPORT)
public class PlayerTeleportPacketIn implements PacketListener<PlayerTeleportPacketIn.PlayerTeleportPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final String mapName = clientHandler.readString();
        final int x = clientHandler.readInt();
        final int y = clientHandler.readInt();
        final byte facingDirection = clientHandler.readByte();

        return new PlayerTeleportPacket(entityId, new Location(mapName, x, y), MoveDirection.getDirection(facingDirection));
    }

    @Override
    public void onEvent(PlayerTeleportPacket packetData) {
        println(getClass(), "ID: " + packetData.entityId);
        println(getClass(), "MapName: " + packetData.teleportLocation.getMapName() + "X: " + packetData.teleportLocation.getX() + ", Y: " + packetData.teleportLocation.getY() + ", Facing: " + packetData.facingDirection.toString());

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        if (playerClient != null && packetData.entityId == playerClient.getServerEntityID()) {
            // Teleport the client player to said location
            // TODO: Check to see if we need to change the map
            teleportMovingEntity(playerClient, packetData.teleportLocation, packetData.facingDirection);
            println(getClass(), "Teleported client player!");

        } else if (EntityManager.getInstance().getMovingEntity(packetData.entityId) != null) {
            MovingEntity movingEntity = EntityManager.getInstance().getMovingEntity(packetData.entityId);
            teleportMovingEntity(movingEntity, packetData.teleportLocation, packetData.facingDirection);
            println(getClass(), "Teleported moving entity!");

        } else {
            println(getClass(), "Something should have teleported??");
        }
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
    }

    @AllArgsConstructor
    class PlayerTeleportPacket extends PacketData {
        private final short entityId;
        private final Location teleportLocation;
        private final MoveDirection facingDirection;
    }
}