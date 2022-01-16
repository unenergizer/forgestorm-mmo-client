package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WangType {
    TYPE_16("BW4=", "=0"),
    TYPE_48("BW16=", "=208");

    private final String prefix;
    private final String defaultWangTileImageId;
}
