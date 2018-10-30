package com.valenguard.client.network.packet.in;

import com.valenguard.client.ClientConstants;
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
        final int tileX = clientHandler.readInt();
        final int tileY = clientHandler.readInt();
        final String name = clientHandler.readString();
        final MoveDirection facingMoveDirection = MoveDirection.getDirection(clientHandler.readByte());
        final float moveSpeed = clientHandler.readFloat();
        // todo add entity types and some crazy attribute stuff and feel good and give ourselves a coke
        final short entityType = clientHandler.readShort();

        Entity entity = getEntityByType(entityId);
        entity.setServerEntityID(entityId);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        System.out.println("entity type : " + entity.getClass().getSimpleName());
        System.out.println("entity id : " + entityId);
        System.out.println("Tile X: " + tileX);
        System.out.println("Tile Y: " + tileY);
        System.out.println("entity name : " + name);
        System.out.println("Move Speed : " + moveSpeed);

        entity.setMapName(playerClient.getMapName());
        entity.setCurrentMapLocation(new Location(playerClient.getMapName(), tileX, tileY));
        entity.setFutureMapLocation(new Location(playerClient.getMapName(), tileX, tileY));
        entity.setDrawX(tileX * ClientConstants.TILE_SIZE);
        entity.setDrawY(tileY * ClientConstants.TILE_SIZE);
        entity.setFacingMoveDirection(facingMoveDirection);
        entity.setMoveSpeed(moveSpeed);

        EntityManager.getInstance().addEntity(entityId, entity);
    }

    private Entity getEntityByType(short entityId) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        return playerClient.getServerEntityID() == entityId ? playerClient : new Entity();
    }

}
