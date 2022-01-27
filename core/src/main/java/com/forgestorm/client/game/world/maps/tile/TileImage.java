package com.forgestorm.client.game.world.maps.tile;


import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.tile.properties.AbstractTileProperty;
import com.forgestorm.shared.game.world.maps.Tags;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;


public class TileImage {

    private static final transient boolean PRINT_DEBUG = false;

    @Getter
    private final transient int imageId;

    @Getter
    private final String fileName;

    @Getter
    @Setter
    private transient TileAnimation tileAnimation;

    @Getter
    @Setter
    private Map<TilePropertyTypes, AbstractTileProperty> tileProperties;

    private List<String> tagsList;

    @Getter
    @Setter
    private LayerDefinition layerDefinition;

    public TileImage(int imageId, String fileName, LayerDefinition layerDefinition) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.layerDefinition = layerDefinition;

        println(getClass(), "---- NEW TILE IMAGE CREATED ----", false, PRINT_DEBUG);
        println(getClass(), "ImageID: " + imageId, false, PRINT_DEBUG);
        println(getClass(), "FileName: " + fileName, false, PRINT_DEBUG);
    }

    public TileImage(TileImage tileImage) {
        this.imageId = tileImage.getImageId();
        this.fileName = tileImage.getFileName();
        this.layerDefinition = tileImage.getLayerDefinition();

        if (tileImage.getTileAnimation() != null) {
            this.tileAnimation = new TileAnimation(tileImage.tileAnimation);
        }

        // Copy tile properties
        if (tileImage.getTileProperties() != null) {
            for (AbstractTileProperty entry : tileImage.getTileProperties().values()) {
                setCustomTileProperty(entry);
            }
        }

        // Copy Tags List
        if (tileImage.tagsList != null) {
            for (String tag : tileImage.tagsList) {
                addTag(Tags.valueOf(tag));
            }
        }
    }

    public void addTag(Tags tag) {
        if (tag == Tags.AN_UNUSED_TAG) return;
        if (tagsList == null) tagsList = new ArrayList<>();
        if (containsTag(tag)) return;
        tagsList.add(tag.name());
    }

    public boolean containsTag(Tags tag) {
        if (tagsList == null) return false;
        for (String s : tagsList) if (s.equals(tag.name())) return true;
        return false;
    }

    public void removeTag(Tags tag) {
        if (tagsList == null) return;
        tagsList.remove(tag.name());
    }

    public boolean containsProperty(TilePropertyTypes tilePropertyType) {
        if (tileProperties == null || tileProperties.isEmpty()) return false;
        return tileProperties.containsKey(tilePropertyType);
    }

    public AbstractTileProperty getProperty(TilePropertyTypes tilePropertyTypes) {
        return this.tileProperties.get(tilePropertyTypes);
    }

    public void setCustomTileProperty(AbstractTileProperty customTileProperty) {
        // Only create an instance of the HashMap here!
        // Doing so will keep the YAML saving code from producing empty brackets
        // in the TileProperties.yaml document.
        if (tileProperties == null) {
            tileProperties = new HashMap<>();
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
            println(getClass(), "LayerDefinition: " + layerDefinition, false);

            for (AbstractTileProperty abstractTileProperty : tileProperties.values()) {
                println(getClass(), "Property: " + abstractTileProperty.getTilePropertyType().toString());
            }
        }
    }

    public TileImage getAnimationFrame() {
        if (tileAnimation == null) return this;

        int activeFrame = tileAnimation.getActiveFrame();

        // Return if their is no active frame
        if (activeFrame == -1) return this;

        return ClientMain.getInstance().getWorldBuilder().getTileImage(tileAnimation.getAnimationFrame(activeFrame).getTileId());
    }

    public int getWidth() {
        return ClientMain.getInstance().getWorldBuilder().getWorldTileImages().findRegion(fileName).originalWidth;
    }

    public int getHeight() {
        return ClientMain.getInstance().getWorldBuilder().getWorldTileImages().findRegion(fileName).originalHeight;
    }
}