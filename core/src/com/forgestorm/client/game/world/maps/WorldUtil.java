package com.forgestorm.client.game.world.maps;


import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;

public class WorldUtil {

    /**
     * Test to see if the tile/coordinate can be walked on.
     *
     * @param entityX The X grid coordinate a entity is attempting to playerMove to.
     * @param entityY The Y grid coordinate a entity is attempting to playerMove to.
     * @return True if the tile/coordinate is walkable. False otherwise.
     */
    public static boolean isTraversable(int entityX, int entityY) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.findChunk(entityX, entityY);
        if (worldChunk == null) return false;

        int localX = entityX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = entityY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        return worldChunk.isTraversable(localX, localY);
    }

    /**
     * Checks to see if a {@link Warp} exists at this location.
     *
     * @param entityX The X location in world coordinates.
     * @param entityY The Y location in world coordinates.
     * @return True if a warp exists, and false otherwise.
     */
    public static boolean isWarp(int entityX, int entityY) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        return gameWorld.getWarp(entityX, entityY) != null;
    }
}
