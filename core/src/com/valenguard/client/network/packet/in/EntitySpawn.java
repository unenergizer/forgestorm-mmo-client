package com.valenguard.client.network.packet.in;

import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;

public class EntitySpawn {

    @Opcode(getOpcode = Opcodes.SPAWN_ENTITY)
    public void spawnEntity(ClientHandler clientHandler) {
//        final int entityId = clientHandler.readInt();
//        final int tileX = clientHandler.readInt();
//        final int tileY = clientHandler.readInt();
//        final int health = clientHandler.readInt();
//        final int level = clientHandler.readInt();
//        final String name = clientHandler.readString();
//
//        Entity entity = new Entity();
//        entity.setEntityId(entityId);
//        entity.setTileX(tileX);
//        entity.setTileY(tileY);
//        entity.setFutureX(tileX);
//        entity.setFutureY(tileY);
//        entity.setDrawX(tileX * ClientConstants.TILE_SIZE);
//        entity.setDrawY(tileX * ClientConstants.TILE_SIZE);
//
//        EntityManager.getInstance().addEntity();
//
//        // Checking if it is the player to notify the game to start rendering.
//        if (Valenguard.getInstance().getPlayerClient().getEntityId() == entityId) {
//            // TODO NOTIFY GAME SCREEN
//        }
    }

}
