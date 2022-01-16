package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.WangTileProperty;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Tile;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypes;

public abstract class AbstractWangTile {

    /**
     * Used to auto update tiles around the supplied X,Y.
     *
     * @param currentLayer The layer we are tiling on.
     * @param x            The X coordinate that needs to be updated.
     * @param y            The Y coordinate that needs to be updated.
     */
    public abstract void updateAroundTile(LayerDefinition currentLayer, int x, int y, short z);

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
    public abstract int autoTile(LayerDefinition currentLayer, int x, int y, short z);

    /**
     * Updates a specific tile as specified.
     *
     * @param currentLayer The layer we are tiling on.
     * @param autoTileID   The ID generated based on surrounding tiles.
     * @param worldX       The world X location.
     * @param worldY       The world Y location.
     */
    protected void updateTile(LayerDefinition currentLayer, int autoTileID, int worldX, int worldY, short worldZ) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        Tile tile = gameWorld.getTile(currentLayer, worldX, worldY, worldZ);
        if (tile == null) return;

        TileImage currentTileImage = tile.getTileImage();
        if (currentTileImage == null) return;

        WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
        if (worldBuilder.getSelectedWangTile() == null) return;

        TileImage autoTileImage = worldBuilder.getTileImage(worldBuilder.getSelectedWangTile().getWangRegionNamePrefix() + autoTileID);

        if (autoTileImage == null) return; // If null, this tile ID doesn't exist for this wang set
        if (currentTileImage.getImageId() == ClientConstants.BLANK_TILE_ID) return;
        if (currentTileImage.getImageId() == autoTileImage.getImageId()) return;

        // Check for different wang tiles
        WangTileProperty wangTileProperty = null;
        if (currentTileImage.containsProperty(TilePropertyTypes.WANG_TILE)) {
            wangTileProperty = (WangTileProperty) currentTileImage.getProperty(TilePropertyTypes.WANG_TILE);
        }

        if (wangTileProperty != null) {
            // If the current wang tile ID is not the same as the one that was found,
            // we do not drawl a tile here. This prevents strange artifacts from occurring.
            if (wangTileProperty.getTemporaryWangId() != worldBuilder.getSelectedWangTile().getTemporaryWangId()) {
                return;
            }
        }

        // Set new tile image
        worldBuilder.placeTile(currentLayer, autoTileImage.getImageId(), worldX, worldY, worldZ, true);
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
     * @param worldZ       The world Z location.
     * @return True if the tile type is the same or False if it is not.
     */
    protected boolean detectSameTileType(LayerDefinition currentLayer, int worldX, int worldY, short worldZ) {
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        Tile tile = gameWorld.getTile(currentLayer, worldX, worldY, worldZ);
        if (tile == null) return false;

        TileImage tileImage = tile.getTileImage();
        if (tileImage == null) return false;

        // TODO: Check out of bounds
        if (tileImage.getImageId() == ClientConstants.BLANK_TILE_ID) return false;

        WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
        WangTileProperty wangTileProperty = null;
        if (tileImage.containsProperty(TilePropertyTypes.WANG_TILE)) {
            wangTileProperty = (WangTileProperty) tileImage.getProperty(TilePropertyTypes.WANG_TILE);
        }

        if (wangTileProperty != null) {
            // If the current wang tile ID is not the same as the one that was found,
            // we do not drawl a tile here. This prevents strange artifacts from occurring.
            if (wangTileProperty.getTemporaryWangId() != worldBuilder.getSelectedWangTile().getTemporaryWangId()) {
                return false;
            }
        }

        return tileImage.containsProperty(TilePropertyTypes.WANG_TILE);
    }
}
