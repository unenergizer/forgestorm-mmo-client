package com.forgestorm.client.game.screens.ui.actors.dev.world;


import com.forgestorm.client.game.screens.ui.actors.dev.world.properties.CustomTileProperties;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class TileImage {

    private final transient int imageId;
    private final String fileName;

    private BuildCategory buildCategory;
    private LayerDefinition layerDefinition;
    private CustomTileProperties customTileProperties;

    public TileImage(int imageId, String fileName, BuildCategory buildCategory) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.buildCategory = buildCategory;

        println(getClass(), "---- NEW TILE IMAGE CREATED ----");
        println(getClass(), "ImageID: " + imageId);
        println(getClass(), "FileName: " + fileName);
        println(getClass(), "BuildCategory: " + buildCategory);
    }
}
