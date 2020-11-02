package com.forgestorm.client.game.screens.ui.actors.dev.world;


import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.AbstractTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
@Setter
public class TileImage {

    private static final transient boolean PRINT_DEBUG = false;

    private final transient int imageId;
    private final String fileName;

    private BuildCategory buildCategory;
    private LayerDefinition layerDefinition;
    private Map<TilePropertyTypes, AbstractTileProperty> tileProperties;

    public TileImage(int imageId, String fileName, BuildCategory buildCategory) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.buildCategory = buildCategory;

        println(getClass(), "---- NEW TILE IMAGE CREATED ----", false, PRINT_DEBUG);
        println(getClass(), "ImageID: " + imageId, false, PRINT_DEBUG);
        println(getClass(), "FileName: " + fileName, false, PRINT_DEBUG);
        println(getClass(), "BuildCategory: " + buildCategory, false, PRINT_DEBUG);
    }

    public boolean containsProperty(TilePropertyTypes tilePropertyType) {
        return tileProperties.containsKey(tilePropertyType);
    }

    public AbstractTileProperty getProperty(TilePropertyTypes tilePropertyTypes) {
        return this.tileProperties.get(tilePropertyTypes);
    }

    public void setCustomTileProperty(AbstractTileProperty customTileProperty) {
        if (tileProperties == null) tileProperties = new HashMap<TilePropertyTypes, AbstractTileProperty>();
        if (tileProperties.containsKey(customTileProperty.getTilePropertyType()))
            throw new RuntimeException("TilePropertiesMap already contains this property: " + customTileProperty.getTilePropertyType());
        tileProperties.put(customTileProperty.getTilePropertyType(), customTileProperty);
    }
}
