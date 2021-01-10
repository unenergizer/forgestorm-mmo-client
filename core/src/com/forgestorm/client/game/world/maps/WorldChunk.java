package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameAtlas;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class WorldChunk {

    @Getter
    private final short chunkX, chunkY;

    @Getter
    private Map<LayerDefinition, TileImage[]> layers = new HashMap<LayerDefinition, TileImage[]>();

    private final Map<Integer, Warp> tileWarps = new HashMap<Integer, Warp>();

    public WorldChunk(short chunkX, short chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    public void setTileImages(LayerDefinition layerDefinition, byte section, TileImage[] tileImages) {
        initTileLayer(layerDefinition);

        for (int i = 0; i < tileImages.length; i++) {
            layers.get(layerDefinition)[(ClientConstants.MAX_TILE_GET * section) + i] = tileImages[i];
        }
    }

    public void setTileImage(LayerDefinition layerDefinition, TileImage tileImage, int index) {
        initTileLayer(layerDefinition);
        layers.get(layerDefinition)[index] = tileImage;
    }

    private void initTileLayer(LayerDefinition layerDefinition) {
        if (layers.containsKey(layerDefinition)) return;
        layers.put(layerDefinition, new TileImage[ClientConstants.CHUNK_SIZE * ClientConstants.CHUNK_SIZE]);
    }

    void setTileImage(LayerDefinition layerDefinition, TileImage tileImage, int localX, int localY) {
        initTileLayer(layerDefinition);
        layers.get(layerDefinition)[localX + localY * ClientConstants.CHUNK_SIZE] = tileImage;
    }

    @SuppressWarnings("SameParameterValue")
    TileImage getTileImage(LayerDefinition layerDefinition, int localX, int localY) {
        return layers.get(layerDefinition)[localX + localY * ClientConstants.CHUNK_SIZE];
    }

    public boolean isTraversable(int localX, int localY) {
        for (TileImage[] tileImages : layers.values()) {
            TileImage tileImage = tileImages[localX + localY * ClientConstants.CHUNK_SIZE];
            if (tileImage == null) continue;
            if (tileImage.containsProperty(TilePropertyTypes.COLLISION_BLOCK)) return false;
        }
        return true;
    }

    public void clearTileWarps() {
        tileWarps.clear();
    }

    public void addTileWarp(short localX, short localY, Warp warp) {
        addTileWarp((localX << 16) | (localY & 0xFFFF), warp);
    }

    public void addTileWarp(int chunkLocation, Warp warp) {
        tileWarps.put(chunkLocation, warp);
    }

    Warp getTileWarp(short localX, short localY) {
        if (tileWarps.containsKey((localX << 16) | (localY & 0xFFFF))) {
            return tileWarps.get((localX << 16) | (localY & 0xFFFF));
        }
        return null;
    }

    public int getNumberOfWarps() {
        return tileWarps.size();
    }

    void renderBottomLayers(Batch batch) {
        // Render layer from most bottom, going up.
        renderLayer(layers.get(LayerDefinition.BACKGROUND), batch);
        renderLayer(layers.get(LayerDefinition.GROUND), batch);
        renderLayer(layers.get(LayerDefinition.GROUND_DECORATION), batch);
    }

    void renderDecorationLayer(Batch batch) {
        renderLayer(layers.get(LayerDefinition.COLLIDABLES), batch);
        renderLayer(layers.get(LayerDefinition.WALL_DECORATION), batch);
    }

    void renderOverheadLayer(Batch batch) {
        renderLayer(layers.get(LayerDefinition.ROOF), batch);
    }

    private void renderLayer(TileImage[] layerTiles, Batch batch) {
        if (layerTiles == null) return;

        // Make the width and height of a given tile just a tad bit larger
        // than it normally would be to prevent most tearing issues.
        final float TILE_SIZE_FIX = 0.005F;

        for (int y = 0; y < ClientConstants.CHUNK_SIZE; y++) {
            for (int x = 0; x < ClientConstants.CHUNK_SIZE; x++) {

                TileImage tileImage = layerTiles[x + y * ClientConstants.CHUNK_SIZE];
                if (tileImage == null) continue;
                FileManager fileManager = ClientMain.getInstance().getFileManager();
                TextureAtlas atlas = fileManager.getAtlas(GameAtlas.TILES);
                TextureRegion textureRegion = atlas.findRegion(tileImage.getFileName());

                float rx = (x + chunkX * ClientConstants.CHUNK_SIZE) * ClientConstants.TILE_SIZE;
                float ry = (y + chunkY * ClientConstants.CHUNK_SIZE) * ClientConstants.TILE_SIZE;

                batch.draw(textureRegion,
                        rx,
                        ry,
                        textureRegion.getRegionWidth() + TILE_SIZE_FIX,
                        textureRegion.getRegionHeight() + TILE_SIZE_FIX);
            }
        }
    }
}
