package com.forgestorm.client.game.world.maps;


import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

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
    public static boolean isDoor(int entityX, int entityY, short worldZ) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
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
     * @return True if a warp exists, and false otherwise.
     */
    public static boolean isWarp(int entityX, int entityY) {
        return getWarp(entityX, entityY) != null;
    }

    /**
     * Gets a {@link Warp} if it exists at this location.
     *
     * @param entityX The X location in world coordinates.
     * @param entityY The Y location in world coordinates.
     * @return Returns a Warp if one exists.
     */
    public static Warp getWarp(int entityX, int entityY) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        return gameWorld.getWarp(entityX, entityY);
    }
}
