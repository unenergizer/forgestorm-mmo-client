package com.valenguard.client.network.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.MoveDirection;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketListener;

public class EntitySpawn implements PacketListener {

    @Opcode(getOpcode = Opcodes.ENTITY_SPAWN)
    public void spawnEntity(ClientHandler clientHandler) {
        final short entityId = clientHandler.readShort();
        final String mapName = clientHandler.readString();
        final int tileX = clientHandler.readInt();
        final int tileY = clientHandler.readInt();
        final String entityName = clientHandler.readString();
        final MoveDirection facingMoveDirection = MoveDirection.getDirection(clientHandler.readByte());
        final float moveSpeed = clientHandler.readFloat();
        // todo add entity types and some crazy attribute stuff and feel good and give ourselves a coke
        final short entityType = clientHandler.readShort();

        Entity entity = getEntityByType(entityId);
        entity.setServerEntityID(entityId);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        System.out.println("entity type : " + entity.getClass().getSimpleName());
        System.out.println("entity id : " + entityId);
        System.out.println("map name : " + mapName);
        System.out.println("Tile X: " + tileX);
        System.out.println("Tile Y: " + tileY);
        System.out.println("entity entityName : " + entityName);
        System.out.println("Move Speed : " + moveSpeed);

        // Check if warping..
        if (playerClient.getCurrentMapLocation() != null && !playerClient.getCurrentMapLocation().getMapName().equalsIgnoreCase(mapName)) {
            System.out.println("Warping...");
            entity.setMapName(mapName);

            // DO MAP CHANGE....
            Valenguard.gameScreen.setTiledMap(ClientConstants.MAP_DIRECTORY + "/" + mapName + ".tmx");
        } else {
            System.out.println("First spawning");
            // TODO: Remove this is retarted....
            entity.setMapName(playerClient.getMapName());
        }

        entity.setCurrentMapLocation(new Location(playerClient.getMapName(), tileX, tileY));
        entity.setFutureMapLocation(new Location(playerClient.getMapName(), tileX, tileY));
        entity.setDrawX(tileX * ClientConstants.TILE_SIZE);
        entity.setDrawY(tileY * ClientConstants.TILE_SIZE);
        entity.setFacingDirection(facingMoveDirection);
        entity.setMoveSpeed(moveSpeed);

        entity.initAnimation();

        EntityManager.getInstance().addEntity(entityId, entity);
    }

    private Entity getEntityByType(short entityId) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        return playerClient.getServerEntityID() == entityId ? playerClient : new Entity();
    }

}
