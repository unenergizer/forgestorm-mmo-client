package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

public interface WorldEdit {

    void applyPropertyToWorld(TileImage tileImage, LayerDefinition layerDefinition, int worldX, int worldY);

    void removePropertyToWorld(TileImage tileImage, LayerDefinition layerDefinition, int worldX, int worldY);
}
