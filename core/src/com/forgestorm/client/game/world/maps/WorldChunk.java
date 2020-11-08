package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameAtlas;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class WorldChunk {

    @Getter
    private final short chunkX, chunkY;

    @Setter
    @Getter
    private Map<LayerDefinition, TileImage[]> layers;

    private final Map<Integer, Warp> tileWarps = new HashMap<Integer, Warp>();

    public WorldChunk(short chunkX, short chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    void setTileImage(LayerDefinition layerDefinition, TileImage tileImage, int localX, int localY) {
        layers.get(layerDefinition)[localX + localY * ClientConstants.CHUNK_SIZE] = tileImage;
    }

    TileImage getTileImage(LayerDefinition layerDefinition, int localX, int localY) {
        return layers.get(layerDefinition)[localX + localY * ClientConstants.CHUNK_SIZE];
    }

    public void addTileWarp(short localX, short localY, Warp warp) {
        tileWarps.put((localX << 16) | (localY & 0xFFFF), warp);
    }

    Warp getWarp(short localX, short localY) {
        if (tileWarps.containsKey((localX << 16) | (localY & 0xFFFF))) {
            return tileWarps.get((localX << 16) | (localY & 0xFFFF));
        }
        return null;
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
                        ClientConstants.TILE_SIZE + TILE_SIZE_FIX,
                        ClientConstants.TILE_SIZE + TILE_SIZE_FIX);
            }
        }
    }
}
