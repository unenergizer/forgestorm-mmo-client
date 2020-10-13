package com.forgestorm.client.game.screens.ui.actors.dev.world;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileImage {

    private final int imageId;
    private final String fileName;
    private final TileType tileType;

    private TileLayers tileLayers;

    public TileImage(int imageId, String fileName, TileType tileType) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.tileType = tileType;
    }
}
