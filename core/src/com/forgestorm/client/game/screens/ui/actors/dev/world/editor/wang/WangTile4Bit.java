package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import com.forgestorm.client.game.world.maps.building.LayerDefinition;

public class WangTile4Bit extends AbstractWangTile {

    @Override
    public void updateAroundTile(LayerDefinition currentLayer, final int x, final int y) {

        // Calculate the auto tile ID that's need
        int north = autoTile(currentLayer, x, y + 1);
        int south = autoTile(currentLayer, x, y - 1);
        int west = autoTile(currentLayer, x - 1, y);
        int east = autoTile(currentLayer, x + 1, y);

        // Update each tile as needed
        updateTile(currentLayer, north, x, y + 1);
        updateTile(currentLayer, south, x, y - 1);
        updateTile(currentLayer, west, x - 1, y);
        updateTile(currentLayer, east, x + 1, y);
    }

    @Override
    public int autoTile(LayerDefinition currentLayer, int x, int y) {

        // Directional Check
        boolean northTile = detectSameTileType(currentLayer, x, y - 1);
        boolean southTile = detectSameTileType(currentLayer, x, y + 1);
        boolean westTile = detectSameTileType(currentLayer, x - 1, y);
        boolean eastTile = detectSameTileType(currentLayer, x + 1, y);

        // 8 bit bit masking calculation using directional check booleans values
        return boolToInt(northTile) //* 1
                + boolToInt(westTile) * 2
                + boolToInt(eastTile) * 4
                + boolToInt(southTile) * 8;
    }
}
