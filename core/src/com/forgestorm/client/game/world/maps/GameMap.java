package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

@Getter
@Setter
public class GameMap {
    private String mapName;
    private int mapWidth;
    private int mapHeight;
    private Map<LayerDefinition, TileImage[]> layers;
    private Color backgroundColor;

    private Texture parallaxBackground;
    private int parallaxX, parallaxY;

    private final Map<Integer, Warp> tileWarps = new HashMap<Integer, Warp>();

    public TileImage[] getLayer(LayerDefinition layerDefinition) {
        return layers.get(layerDefinition);
    }

    public void setTileImage(LayerDefinition layerDefinition, TileImage tileImage, int locationX, int locationY) {
        layers.get(layerDefinition)[locationX + locationY * mapWidth] = tileImage;
    }

    public void addTileWarp(short x, short y, Warp warp) {
        tileWarps.put((x << 16) | (y & 0xFFFF), warp);
    }

    Warp getWarp(short x, short y) {
        if (tileWarps.containsKey((x << 16) | (y & 0xFFFF))) {
            return tileWarps.get((x << 16) | (y & 0xFFFF));
        }
        return null;
    }

    public void drawParallax(SpriteBatch spriteBatch) {
        if (parallaxBackground == null) return;
        parallaxX += 2;
        parallaxY -= 3;
        if (parallaxX >= parallaxBackground.getWidth()) parallaxX = 0;
        if (parallaxY <= -parallaxBackground.getHeight()) parallaxY = 0;
        spriteBatch.draw(parallaxBackground, -parallaxBackground.getWidth(), -parallaxBackground.getHeight(),
                parallaxX, parallaxY,
                Gdx.graphics.getWidth() + parallaxBackground.getWidth() * 2,
                Gdx.graphics.getHeight() + parallaxBackground.getHeight() * 2);
    }

    public void renderBottomLayers(Batch batch) {
        // Render layer from most bottom, going up.
        renderLayer(layers.get(LayerDefinition.BACKGROUND), batch);
        renderLayer(layers.get(LayerDefinition.GROUND), batch);
        renderLayer(layers.get(LayerDefinition.GROUND_DECORATION), batch);
    }

    public void renderDecorationLayer(Batch batch) {
        renderLayer(layers.get(LayerDefinition.WALL), batch);
        renderLayer(layers.get(LayerDefinition.WALL_DECORATION), batch);
    }

    public void renderOverheadLayer(Batch batch) {
        renderLayer(layers.get(LayerDefinition.ROOF), batch);
    }

    private void renderLayer(TileImage[] layer, Batch batch) {

        // Make the width and height of a given tile just a tad bit larger
        // than it normally would be to prevent most tearing issues.
        final float TILE_SIZE_FIX = 0.005F;

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {

                float rx = x * ClientConstants.TILE_SIZE;
                float ry = y * ClientConstants.TILE_SIZE;

                TileImage tileImage = layer[x + y * mapWidth];
                if (tileImage == null) continue;
                FileManager fileManager = ClientMain.getInstance().getFileManager();
                TextureAtlas atlas = fileManager.getAtlas(GameAtlas.TILES);
                TextureRegion textureRegion = atlas.findRegion(tileImage.getFileName());

                batch.draw(textureRegion, rx, ry, ClientConstants.TILE_SIZE + TILE_SIZE_FIX, ClientConstants.TILE_SIZE + TILE_SIZE_FIX);
            }
        }
    }
}