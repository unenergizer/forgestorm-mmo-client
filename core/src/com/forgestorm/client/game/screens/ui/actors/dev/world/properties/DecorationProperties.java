package com.forgestorm.client.game.screens.ui.actors.dev.world.properties;

import com.forgestorm.client.game.screens.ui.actors.dev.world.DecorationType;

import lombok.Getter;

@Getter
public abstract class DecorationProperties implements CustomTileProperties {

    private final DecorationType decorationType;

    public DecorationProperties(DecorationType decorationType) {
        this.decorationType = decorationType;
    }
}
