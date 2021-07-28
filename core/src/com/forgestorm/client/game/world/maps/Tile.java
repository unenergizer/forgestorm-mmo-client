package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.CollisionBlockProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Tile {

    private final transient List<Integer> collisionParents = new ArrayList<Integer>(0);
    private final LayerDefinition layerDefinition;

    @Getter
    private final String worldName;

    @Getter
    private final int worldX, worldY;

    @Getter
    private TileImage tileImage;

    public Tile(LayerDefinition layerDefinition, String worldName, int worldX, int worldY) {
        this.layerDefinition = layerDefinition;
        this.worldName = worldName;
        this.worldX = worldX;
        this.worldY = worldY;
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

        // DO COLLISION APPLICATION
        if (tileImage.containsProperty(TilePropertyTypes.COLLISION_BLOCK)) {
            CollisionBlockProperty collisionBlockProperty = (CollisionBlockProperty) tileImage.getProperty(TilePropertyTypes.COLLISION_BLOCK);
            collisionBlockProperty.applyPropertyToWorld(tileImage, layerDefinition, worldName, worldX, worldY);
        }
    }

    private void removeTileProperties() {
        if (tileImage == null) return;

        // DO COLLISION REMOVAL
        if (tileImage.containsProperty(TilePropertyTypes.COLLISION_BLOCK)) {
            CollisionBlockProperty collisionBlockProperty = (CollisionBlockProperty) tileImage.getProperty(TilePropertyTypes.COLLISION_BLOCK);
            collisionBlockProperty.removePropertyFromWorld(tileImage, layerDefinition, worldName, worldX, worldY);
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
