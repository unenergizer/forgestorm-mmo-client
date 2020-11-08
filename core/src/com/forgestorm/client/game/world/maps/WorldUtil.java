package com.forgestorm.client.game.world.maps;


import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

public class WorldUtil {

    /**
     * Test to see if the tile/coordinate can be walked on.
     *
     * @param worldX The X grid coordinate a entity is attempting to playerMove to.
     * @param worldY The Y grid coordinate a entity is attempting to playerMove to.
     * @return True if the tile/coordinate is walkable. False otherwise.
     */
    public static boolean isTraversable(int worldX, int worldY) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.findChunk(worldX, worldY);
        if (worldChunk == null) return false;

        int localX = worldX - worldChunk.getChunkX() * ClientConstants.CHUNK_SIZE;
        int localY = worldY - worldChunk.getChunkY() * ClientConstants.CHUNK_SIZE;

        TileImage tileImage = worldChunk.getTileImage(LayerDefinition.COLLIDABLES, localX, localY);
        if (tileImage == null) return true;
        return tileImage.containsProperty(TilePropertyTypes.COLLISION_BLOCK);
    }

    /**
     * Checks to see if a {@link Warp} exists at this location.
     *
     * @param worldX The X location in world coordinates.
     * @param worldY The Y location in world coordinates.
     * @return True if a warp exists, and false otherwise.
     */
    public static boolean isWarp(int worldX, int worldY) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        return gameWorld.getWarp(worldX, worldY) != null;
    }
}
