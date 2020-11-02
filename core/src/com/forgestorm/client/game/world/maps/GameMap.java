package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameAtlas;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameMap {
    private String mapName;
    private int mapWidth;
    private int mapHeight;
    private Map<Integer, TileImage[]> layers;
    private final Color backgroundColor;

    public void renderMap(Batch batch) {
        for (TileImage[] layer : layers.values()) {
            renderLayer(layer, batch);
        }
    }

    private void renderLayer(TileImage[] layer, Batch batch) {

        final float TILE_SIZE = 16.0F;

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {

                float rx = x * TILE_SIZE;
                float ry = y * TILE_SIZE;

                TileImage tileImage = layer[x + y * mapWidth];
                FileManager fileManager = ClientMain.getInstance().getFileManager();
                TextureAtlas atlas = fileManager.getAtlas(GameAtlas.TILES);
                TextureRegion textureRegion = atlas.findRegion(tileImage.getFileName());

                batch.draw(textureRegion, rx, ry, TILE_SIZE, TILE_SIZE);

            }
        }

    }
}