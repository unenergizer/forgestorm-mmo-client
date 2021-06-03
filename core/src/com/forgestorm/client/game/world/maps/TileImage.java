package com.forgestorm.client.game.world.maps;


import com.forgestorm.client.game.screens.ui.actors.dev.world.BuildCategory;
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
    }

    public boolean containsProperty(TilePropertyTypes tilePropertyType) {
        if (tileProperties == null || tileProperties.isEmpty()) return false;
        return tileProperties.containsKey(tilePropertyType);
    }

    public AbstractTileProperty getProperty(TilePropertyTypes tilePropertyTypes) {
        return this.tileProperties.get(tilePropertyTypes);
    }

    public void setCustomTileProperty(AbstractTileProperty customTileProperty) {
        if (customTileProperty == null) {
            println(getClass(), "AbstractTileProperty was null! ", true);
        }

        if (tileProperties == null) {
            tileProperties = new HashMap<TilePropertyTypes, AbstractTileProperty>();
        }

        if (tileProperties.containsKey(customTileProperty.getTilePropertyType())) {
            println(getClass(), "TilePropertiesMap already contains this property: " + customTileProperty.getTilePropertyType());
        } else {
            tileProperties.put(customTileProperty.getTilePropertyType(), customTileProperty);
        }

        if (PRINT_DEBUG) {
            println(getClass(), "---- TILE IMAGE SET PROPERTY ----", false);
            println(getClass(), "ImageID: " + imageId, false);
            println(getClass(), "FileName: " + fileName, false);
            println(getClass(), "BuildCategory: " + buildCategory, false);

            for (AbstractTileProperty abstractTileProperty : tileProperties.values()) {
                println(getClass(), "Property: " + abstractTileProperty.getTilePropertyType().toString());
            }
        }
    }
}
