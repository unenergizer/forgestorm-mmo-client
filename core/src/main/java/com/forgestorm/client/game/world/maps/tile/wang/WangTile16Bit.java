package com.forgestorm.client.game.world.maps.tile.wang;

import com.forgestorm.shared.game.world.maps.building.LayerDefinition;

public class WangTile16Bit extends AbstractWangTile {

    @Override
    public void updateAroundTile(LayerDefinition currentLayer, final int x, final int y, final short z) {

        // Calculate the auto tile ID that's need
        int northWest = autoTile(currentLayer, x - 1, y + 1, z);
        int north = autoTile(currentLayer, x, y + 1, z);
        int northEast = autoTile(currentLayer, x + 1, y + 1, z);

        int west = autoTile(currentLayer, x - 1, y, z);
        int east = autoTile(currentLayer, x + 1, y, z);

        int southWest = autoTile(currentLayer, x - 1, y - 1, z);
        int south = autoTile(currentLayer, x, y - 1, z);
        int southEast = autoTile(currentLayer, x + 1, y - 1, z);

        // Update each tile as needed
        updateTile(currentLayer, northWest, x - 1, y + 1, z);
        updateTile(currentLayer, north, x, y + 1, z);
        updateTile(currentLayer, northEast, x + 1, y + 1, z);

        updateTile(currentLayer, west, x - 1, y, z);
        updateTile(currentLayer, east, x + 1, y, z);

        updateTile(currentLayer, southWest, x - 1, y - 1, z);
        updateTile(currentLayer, south, x, y - 1, z);
        updateTile(currentLayer, southEast, x + 1, y - 1, z);
    }

    @Override
    public int autoTile(LayerDefinition currentLayer, int x, int y, short z) {
        // Directional Check, including corners, returns int
        boolean northTile = detectSameTileType(currentLayer, x, y - 1, z);
        boolean southTile = detectSameTileType(currentLayer, x, y + 1, z);
        boolean westTile = detectSameTileType(currentLayer, x - 1, y, z);
        boolean eastTile = detectSameTileType(currentLayer, x + 1, y, z);
        boolean northWestTile = detectSameTileType(currentLayer, x - 1, y - 1, z) && westTile && northTile;
        boolean northEastTile = detectSameTileType(currentLayer, x + 1, y - 1, z) && northTile && eastTile;
        boolean southWestTile = detectSameTileType(currentLayer, x - 1, y + 1, z) && southTile && westTile;
        boolean southEastTile = detectSameTileType(currentLayer, x + 1, y + 1, z) && southTile && eastTile;

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
