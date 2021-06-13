package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.CollisionBlockProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Tile {

    private final transient List<TileImage> collisionParents = new ArrayList<TileImage>(0);
    private final LayerDefinition layerDefinition;
    private final int worldX, worldY;

    @Getter
    private TileImage tileImage;

    public Tile(LayerDefinition layerDefinition, int worldX, int worldY) {
        this.layerDefinition = layerDefinition;
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public void setTileImage(TileImage tileImage) {
        if (tileImage != null) removeTileImage();

        this.tileImage = tileImage;
        applyTileProperties();
    }

    public void removeTileImage() {
        tileImage = null;
        removeTileProperties();
    }

    private void applyTileProperties() {
        if (tileImage == null) return;

        // DO COLLISION APPLICATION
        if (tileImage.containsProperty(TilePropertyTypes.COLLISION_BLOCK)) {
            CollisionBlockProperty collisionBlockProperty = (CollisionBlockProperty) tileImage.getProperty(TilePropertyTypes.COLLISION_BLOCK);
            collisionBlockProperty.applyPropertyToWorld(tileImage, layerDefinition, worldX, worldY);
        }
    }

    private void removeTileProperties() {
        if (tileImage == null) return;

        // DO COLLISION REMOVAL
        if (tileImage.containsProperty(TilePropertyTypes.COLLISION_BLOCK)) {
            CollisionBlockProperty collisionBlockProperty = (CollisionBlockProperty) tileImage.getProperty(TilePropertyTypes.COLLISION_BLOCK);
            collisionBlockProperty.removePropertyToWorld(tileImage, layerDefinition, worldX, worldY);
        }
    }

    public void addCollision(TileImage parent) {
        collisionParents.add(parent);
    }

    public void removeCollision(TileImage parent) {
        collisionParents.remove(parent);
    }

    public boolean hasCollision() {
        return !collisionParents.isEmpty();
    }
}
