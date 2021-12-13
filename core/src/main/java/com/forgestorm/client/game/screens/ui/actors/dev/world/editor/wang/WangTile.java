package com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang;

import com.forgestorm.client.game.screens.ui.actors.dev.world.BrushSize;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WangTile {

    private final int wangId;
    private final String fileName;
    private final WangType wangType;
    private final String wangRegionNamePrefix;

    @Setter
    private BrushSize brushSize = BrushSize.ONE;

    public WangTile(int wangId, String fileName, WangType wangType) {
        this.wangId = wangId;
        this.fileName = fileName;
        this.wangType = wangType;

        wangRegionNamePrefix = wangType.getPrefix() + "-" + fileName + "-";
    }
}
