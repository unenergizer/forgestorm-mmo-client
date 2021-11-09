package com.forgestorm.client.game.screens.ui.actors.dev.world;

public enum DecorationType {
    BED,
    CHAIR,
    CONTAINER,
    TABLE,
    UNDEFINED;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
