package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum WangType {
    TYPE_16("BW4"),
    TYPE_48("BW16");

    @Getter
    private final String prefix;
}
