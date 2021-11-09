package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import com.forgestorm.shared.game.world.maps.building.LayerDefinition;

public class WangTile4Bit extends AbstractWangTile {

    @Override
    public void updateAroundTile(LayerDefinition currentLayer, final int x, final int y, final short z) {

        // Calculate the auto tile ID that's need
        int north = autoTile(currentLayer, x, y + 1, z);
        int south = autoTile(currentLayer, x, y - 1, z);
        int west = autoTile(currentLayer, x - 1, y, z);
        int east = autoTile(currentLayer, x + 1, y, z);

        // Update each tile as needed
        updateTile(currentLayer, north, x, y + 1, z);
        updateTile(currentLayer, south, x, y - 1, z);
        updateTile(currentLayer, west, x - 1, y, z);
        updateTile(currentLayer, east, x + 1, y, z);
    }

    @Override
    public int autoTile(LayerDefinition currentLayer, int x, int y, short z) {

        // Directional Check
        boolean northTile = detectSameTileType(currentLayer, x, y - 1, z);
        boolean southTile = detectSameTileType(currentLayer, x, y + 1, z);
        boolean westTile = detectSameTileType(currentLayer, x - 1, y, z);
        boolean eastTile = detectSameTileType(currentLayer, x + 1, y, z);

        // 8 bit bit masking calculation using directional check booleans values
        return boolToInt(northTile) //* 1
                + boolToInt(westTile) * 2
                + boolToInt(eastTile) * 4
                + boolToInt(southTile) * 8;
    }
}
