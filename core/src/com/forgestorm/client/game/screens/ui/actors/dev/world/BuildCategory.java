package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BuildCategory {
    DECORATION(LayerDefinition.WALL_DECORATION),
    WALKABLE(LayerDefinition.GROUND_DECORATION),
    WALL(LayerDefinition.COLLIDABLES),
    ROOF(LayerDefinition.ROOF),
    UNDEFINED(LayerDefinition.GROUND_DECORATION);

    public LayerDefinition layerDefinition;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
