package com.valenguard.client.network.packet.in;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.entities.Direction;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
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
        final Direction facingDirection = Direction.getDirection(clientHandler.readByte());
        // todo add entity types and some crazy attribute stuff and feel good and give ourselves a coke
        final short entityType = clientHandler.readShort();

        Entity entity = getEntityByType(entityId);
        entity.setEntityId(entityId);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        System.out.println("entity type : " + entity.getClass().getSimpleName());
        System.out.println("entity id : " + entityId);
        System.out.println("Tile X: " + tileX);
        System.out.println("Tile Y: " + tileY);
        System.out.println("entity name : " + name);

        // moveDirection = inputStream.getDirection();
        // if (moveDirection != Direction.STOP) then they must be moving
        // ...
        // float realX = inputStream.readFloat();
        // float realY = inputStream.readFloat();
        // ...
        // MovementSystem.addEntity(entity);

        // todo: exchange for actual map getting
        entity.setCurrentMapLocation(new Location(playerClient.getMapName(), tileX, tileY));
        entity.setFutureMapLocation(new Location(playerClient.getMapName(), tileX, tileY));
        entity.setMoveDirection(Direction.STOP);
        entity.setPredictedDirection(Direction.STOP);
        entity.setDrawX(tileX * ClientConstants.TILE_SIZE);
        entity.setDrawY(tileY * ClientConstants.TILE_SIZE);
        entity.setFacingDirection(facingDirection);

        EntityManager.getInstance().addEntity(entityId, entity);
    }

    private Entity getEntityByType(short entityId) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Entity entity = playerClient.getEntityId() == entityId ? playerClient : new Entity();
        return entity;
    }

}
