package com.forgestorm.shared.game.world.tile.properties;

import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;

public interface WorldEdit {

    void applyPropertyToWorld(WorldChunk worldChunk, TileImage tileImage, LayerDefinition layerDefinition, String worldName, int worldX, int worldY, short worldZ);

    void removePropertyFromWorld(WorldChunk worldChunk, TileImage tileImage, LayerDefinition layerDefinition, String worldName, int worldX, int worldY, short worldZ);
}
