package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties;

public enum TilePropertyTypes {
    CURSOR_DRAW_OVER_TILE,
    COLLISION_BLOCK,
    DOOR,
    INTERACTIVE_CONTAINER,
    WANG_TILE,
    BLOCK_MOVE_DIRECTION,
    JUMP_TO_DIRECTION,
    LADDER,
    WATER,
    INTERACT_DAMAGE,
    WALK_OVER_SOUND;

    @Override
    public String toString() {
        String name = name().toLowerCase().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
