package com.forgestorm.client.game.world.maps;


import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.shared.game.world.maps.Warp;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;

public class WorldUtil {

    /**
     * Test to see if the tile/coordinate can be walked on.
     *
     * @param entityX The X grid coordinate a entity is attempting to playerMove to.
     * @param entityY The Y grid coordinate a entity is attempting to playerMove to.
     * @return True if the tile/coordinate is walkable. False otherwise.
     */
    public static boolean isTraversable(ClientMain clientMain, int entityX, int entityY) {
        GameWorld gameWorld = clientMain.getWorldManager().getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.findChunk(entityX, entityY);
        // TODO: Setting true so player can walk into new chunk. Not all worlds can generate chunks.
        //  So distinguish those here.
        if (worldChunk == null) return true;

        int localX = entityX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = entityY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        return worldChunk.isTraversable(localX, localY);
    }

    /**
     * Test to see if the tile/coordinate is a door.
     *
     * @param entityX The X grid coordinate a entity is attempting to playerMove to.
     * @param entityY The Y grid coordinate a entity is attempting to playerMove to.
     * @param worldZ  The Z axis an entity is already on.
     * @return True if the tile/coordinate is a door. False otherwise.
     */
    public static boolean isDoor(ClientMain clientMain, int entityX, int entityY, short worldZ) {
        GameWorld gameWorld = clientMain.getWorldManager().getCurrentGameWorld();
        Tile tile = gameWorld.getTile(LayerDefinition.WORLD_OBJECTS, entityX, entityY, worldZ);
        if (tile == null) return false;
        if (tile.getTileImage() == null) return false;
        return tile.getTileImage().containsProperty(TilePropertyTypes.DOOR);
    }

    /**
     * Checks to see if a {@link Warp} exists at this location.
     *
     * @param entityX The X location in world coordinates.
     * @param entityY The Y location in world coordinates.
     * @param entityZ The Z location in world coordinates.
     * @return True if a warp exists, and false otherwise.
     */
    public static boolean isWarp(ClientMain clientMain, int entityX, int entityY, short entityZ) {
        return getWarp(clientMain, entityX, entityY, entityZ) != null;
    }

    /**
     * Gets a {@link Warp} if it exists at this location.
     *
     * @param entityX The X location in world coordinates.
     * @param entityY The Y location in world coordinates.
     * @param entityZ The Z location in world coordinates.
     * @return Returns a Warp if one exists.
     */
    public static Warp getWarp(ClientMain clientMain, int entityX, int entityY, short entityZ) {
        GameWorld gameWorld = clientMain.getWorldManager().getCurrentGameWorld();
        return gameWorld.getWarp(entityX, entityY, entityZ);
    }
}
