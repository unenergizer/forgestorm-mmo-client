package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;

public abstract class AbstractWangTile {

    /**
     * Used to auto update tiles around the supplied X,Y.
     *
     * @param currentLayer The layer we are tiling on.
     * @param x            The X coordinate that needs to be updated.
     * @param y            The Y coordinate that needs to be updated.
     */
    public abstract void updateAroundTile(LayerDefinition currentLayer, int x, int y);

    /**
     * An implementation used to calculate what tile is
     * being updated. This checks tiles around the given
     * coordinates to see what this X,Y needs to change
     * to.
     *
     * @param currentLayer The layer we are tiling on.
     * @param x            The X coordinate to check.
     * @param y            The Y coordinate to check.
     * @return A index total used to see what image needs
     * to be used.
     */
    public abstract int autoTile(LayerDefinition currentLayer, int x, int y);

    /**
     * Updates a specific tile as specified.
     *
     * @param currentLayer The layer we are tiling on.
     * @param autoTileID   The ID generated based on surrounding tiles.
     * @param worldX       The world X location.
     * @param worldY       The world Y location.
     */
    protected void updateTile(LayerDefinition currentLayer, int autoTileID, int worldX, int worldY) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        TileImage currentTileImage = gameWorld.getTileImage(currentLayer, worldX, worldY);

        WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
        TileImage autoTileImage = worldBuilder.getTileImage(worldBuilder.getWangRegionNamePrefix() + autoTileID);

        if (currentTileImage == null) return;
        if (currentTileImage.getImageId() == ClientConstants.BLANK_TILE_ID) return;
        if (currentTileImage.getImageId() == autoTileImage.getImageId()) return;

        // Check for different wang tiles
        WangTile wangTileFound = worldBuilder.findWangTile(currentTileImage);

        if (wangTileFound != null) {
            // If the current wang tile ID is not the same as the one that was found,
            // we do not drawl a tile here. This prevents strange artifacts from occurring.
            if (wangTileFound.getWangId() != worldBuilder.getCurrentWangId()) return;
        }

        // Set new tile image
        worldBuilder.placeTile(currentLayer, autoTileImage.getImageId(), worldX, worldY, true
        );
    }

    /**
     * Converts a boolean to a numerical value.
     *
     * @param b the boolean to test.
     * @return 1 if true or 0 if false.
     */
    protected int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    /**
     * Detect if this tile position is using the same type of tile as the main one being tested
     *
     * @param currentLayer The layer we are tiling on.
     * @param worldX       The world X location.
     * @param worldY       The world Y location.
     * @return True if the tile type is the same or False if it is not.
     */
    protected boolean detectSameTileType(LayerDefinition currentLayer, int worldX, int worldY) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        TileImage tileImage = gameWorld.getTileImage(currentLayer, worldX, worldY);

        // TODO: Check out of bounds
        if (tileImage == null) return false;
        if (tileImage.getImageId() == ClientConstants.BLANK_TILE_ID) return false;

        WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
        WangTile wangTile = worldBuilder.findWangTile(tileImage);

        if (wangTile != null) {
            if (wangTile.getWangId() != worldBuilder.getCurrentWangId()) {
                System.out.println("Different type detected");
                return false; // Test for different wang tile types
            }
        }

        return tileImage.containsProperty(TilePropertyTypes.WANG_TILE);
    }
}
