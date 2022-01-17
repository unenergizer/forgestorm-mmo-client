package com.forgestorm.shared.game.world.maps.tile.properties;

public enum TilePropertyTypes {

    BLOCK_MOVE_DIRECTION,
    COLLISION_BLOCK,
    CURSOR_DRAW_OVER_TILE,
    DOOR,
    INTERACTIVE_CONTAINER,
    INTERACT_DAMAGE,
    INTERIOR_STAIRS_PROPERTY,
    JUMP_TO_DIRECTION,
    LADDER,
    WALK_OVER_SOUND,
    WATER,
    WANG_TILE;

    @Override
    public String toString() {
        String name = name().toLowerCase().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
