package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import lombok.Getter;

@Getter
public class WangTile {

    private final int wangId;
    private final String fileName;
    private final WangType wangType;

    public WangTile(int wangId, String fileName, WangType wangType) {
        this.wangId = wangId;
        this.fileName = fileName;
        this.wangType = wangType;
    }
}
