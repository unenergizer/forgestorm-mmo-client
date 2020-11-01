package com.forgestorm.client.game.screens.ui.actors.dev.world;

public enum BuildCategory {
    DECORATION,
    WALKABLE,
    WALL,
    ROOF,
    UNDEFINED;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
