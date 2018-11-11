package com.valenguard.client.network.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
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
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.Log;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.ENTITY_SPAWN)
public class EntitySpawn implements PacketListener<EntitySpawn.EntitySpawnPacket> {

    private static final boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new EntitySpawnPacket(
                clientHandler.readShort(),
                clientHandler.readInt(),
                clientHandler.readInt(),
                clientHandler.readString(),
                clientHandler.readByte(),
                clientHandler.readFloat(),
                clientHandler.readShort()
        );
    }

    @Override
    public void onEvent(EntitySpawnPacket packetData) {
        String mapName = Valenguard.gameScreen.getGameMapNameFromServer();
        MovingEntity entity;

        Log.println(getClass(), "Game Screen: " + Valenguard.gameScreen, false, PRINT_DEBUG);
        Log.println(getClass(), "Player Session Data: " + Valenguard.gameScreen.getPlayerSessionData(), false, PRINT_DEBUG);

        if (packetData.entityId == Valenguard.gameScreen.getPlayerSessionData().getClientPlayerId()) {
            entity = new PlayerClient();

            PlayerClient playerClient = (PlayerClient) entity;

            AttachableCamera camera = Valenguard.gameScreen.getCamera();

            Log.println(EntitySpawn.class, "Found player. Initializing the player");

            // Attach entity to camera
            camera.attachEntity(playerClient);

            EntityManager.getInstance().setPlayerClient(playerClient);

            Valenguard.gameScreen.getKeyboard().getKeyboardMovement().setInvalidated(false);
            Valenguard.getInstance().getMouseManager().setInvalidate(false);

        } else {
            entity = new MovingEntity();
        }

        entity.setServerEntityID(packetData.entityId);
        entity.setMapName(mapName);

        Log.println(getClass(), "entity type : " + entity.getClass().getSimpleName(), false, PRINT_DEBUG);
        Log.println(getClass(), "entity id : " + packetData.entityId, false, PRINT_DEBUG);
        Log.println(getClass(), "Tile X: " + packetData.tileX, false, PRINT_DEBUG);
        Log.println(getClass(), "Tile Y: " + packetData.tileY, false, PRINT_DEBUG);
        Log.println(getClass(), "entity entityName : " + packetData.entityName, false, PRINT_DEBUG);
        Log.println(getClass(), "Move Speed : " + packetData.moveSpeed, false, PRINT_DEBUG);
        Log.println(getClass(), "Map Name: " + mapName, false, PRINT_DEBUG);

        Log.println(getClass(),"Setting the current and future map locations to: " + new Location(entity.getMapName(), packetData.tileX, packetData.tileY));

        entity.setCurrentMapLocation(new Location(entity.getMapName(), packetData.tileX, packetData.tileY));
        entity.setFutureMapLocation(new Location(entity.getMapName(), packetData.tileX, packetData.tileY));
        entity.setDrawX(packetData.tileX * ClientConstants.TILE_SIZE);
        entity.setDrawY(packetData.tileY * ClientConstants.TILE_SIZE);

        MoveDirection facingDirection = MoveDirection.getDirection(packetData.facingMoveDirectionByte);

        if (facingDirection == MoveDirection.NONE) {
            throw new RuntimeException("The server sent a facing direction of NONE for some reason.");
        }

        entity.setFacingDirection(facingDirection);
        entity.setMoveSpeed(packetData.moveSpeed);

        entity.initAnimation();

        if (!(entity instanceof PlayerClient))
            EntityManager.getInstance().addEntity(packetData.entityId, entity);
    }

    @AllArgsConstructor
    class EntitySpawnPacket extends PacketData {
        private final short entityId;
        private final int tileX;
        private final int tileY;
        private final String entityName;
        private final byte facingMoveDirectionByte;
        private final float moveSpeed;
        private final short entityType;
    }
}
