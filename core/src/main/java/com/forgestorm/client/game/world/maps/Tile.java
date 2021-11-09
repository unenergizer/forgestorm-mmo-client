package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.AbstractTileProperty;
import com.forgestorm.client.game.world.WorldObject;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.tile.properties.WorldEdit;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Tile extends WorldObject {

    private final transient List<Integer> collisionParents = new ArrayList<Integer>(0);
    private final LayerDefinition layerDefinition;

    @Getter
    private final String worldName;

    @Getter
    private final WorldChunk worldChunk;

    @Getter
    private final int worldX, worldY;

    @Getter
    private final short worldZ;

    @Getter
    private TileImage tileImage;

    public Tile(LayerDefinition layerDefinition, String worldName, WorldChunk worldChunk, int worldX, int worldY, short worldZ) {
        this.layerDefinition = layerDefinition;
        this.worldName = worldName;
        this.worldChunk = worldChunk;
        this.worldX = worldX;
        this.worldY = worldY;
        this.worldZ = worldZ;

        // Set the draw cords
        this.setDrawX(worldX * ClientConstants.TILE_SIZE);
        this.setDrawY(worldY * ClientConstants.TILE_SIZE);
    }

    public void setTileImage(TileImage tileImage) {
        if (this.tileImage != null) removeTileImage();

        this.tileImage = tileImage;
        applyTileProperties();
    }

    public void removeTileImage() {
        removeTileProperties();
        tileImage = null;
    }

    private void applyTileProperties() {
        if (tileImage == null) return;
        if (tileImage.getTileProperties() == null) return;

        // Apply properties to the world
        for (AbstractTileProperty abstractTileProperty : tileImage.getTileProperties().values()) {
            if (abstractTileProperty instanceof WorldEdit) {
                ((WorldEdit) abstractTileProperty).applyPropertyToWorld(worldChunk, tileImage, layerDefinition, worldName, worldX, worldY, worldZ);
            }
        }
    }

    private void removeTileProperties() {
        if (tileImage == null) return;
        if (tileImage.getTileProperties() == null) return;

        // Remove properties from world
        for (AbstractTileProperty abstractTileProperty : tileImage.getTileProperties().values()) {
            if (abstractTileProperty instanceof WorldEdit) {
                ((WorldEdit) abstractTileProperty).removePropertyFromWorld(worldChunk, tileImage, layerDefinition, worldName, worldX, worldY, worldZ);
            }
        }
    }

    public void addCollision(TileImage parent) {
        if (collisionParents.contains(parent.getImageId())) return;
        collisionParents.add(parent.getImageId());
    }

    public void removeCollision(TileImage parent) {
        // Do not remove cast to Integer
        collisionParents.remove((Integer) parent.getImageId());
    }

    public boolean hasCollision() {
        return !collisionParents.isEmpty();
    }
}
