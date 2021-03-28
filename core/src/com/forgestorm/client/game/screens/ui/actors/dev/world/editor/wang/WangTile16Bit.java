package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import com.forgestorm.client.game.world.maps.building.LayerDefinition;

public class WangTile16Bit extends AbstractWangTile {

    @Override
    public void updateAroundTile(LayerDefinition currentLayer, final int x, final int y) {

        // Calculate the auto tile ID that's need
        int northWest = autoTile(currentLayer, x - 1, y + 1);
        int north = autoTile(currentLayer, x, y + 1);
        int northEast = autoTile(currentLayer, x + 1, y + 1);

        int west = autoTile(currentLayer, x - 1, y);
        int east = autoTile(currentLayer, x + 1, y);

        int southWest = autoTile(currentLayer, x - 1, y - 1);
        int south = autoTile(currentLayer, x, y - 1);
        int southEast = autoTile(currentLayer, x + 1, y - 1);

        // Update each tile as needed
        updateTile(currentLayer, northWest, x - 1, y + 1);
        updateTile(currentLayer, north, x, y + 1);
        updateTile(currentLayer, northEast, x + 1, y + 1);

        updateTile(currentLayer, west, x - 1, y);
        updateTile(currentLayer, east, x + 1, y);

        updateTile(currentLayer, southWest, x - 1, y - 1);
        updateTile(currentLayer, south, x, y - 1);
        updateTile(currentLayer, southEast, x + 1, y - 1);
    }

    @Override
    public int autoTile(LayerDefinition currentLayer, int x, int y) {
        // Directional Check, including corners, returns int
        boolean northTile = detectSameTileType(currentLayer, x, y - 1);
        boolean southTile = detectSameTileType(currentLayer, x, y + 1);
        boolean westTile = detectSameTileType(currentLayer, x - 1, y);
        boolean eastTile = detectSameTileType(currentLayer, x + 1, y);
        boolean northWestTile = detectSameTileType(currentLayer, x - 1, y - 1) && westTile && northTile;
        boolean northEastTile = detectSameTileType(currentLayer, x + 1, y - 1) && northTile && eastTile;
        boolean southWestTile = detectSameTileType(currentLayer, x - 1, y + 1) && southTile && westTile;
        boolean southEastTile = detectSameTileType(currentLayer, x + 1, y + 1) && southTile && eastTile;

        // 8 bit bit masking calculation using directional check booleans values
        return boolToInt(northWestTile) // * 1
                + boolToInt(northTile) * 2
                + boolToInt(northEastTile) * 4
                + boolToInt(westTile) * 8
                + boolToInt(eastTile) * 16
                + boolToInt(southWestTile) * 32
                + boolToInt(southTile) * 64
                + boolToInt(southEastTile) * 128;
    }
}
