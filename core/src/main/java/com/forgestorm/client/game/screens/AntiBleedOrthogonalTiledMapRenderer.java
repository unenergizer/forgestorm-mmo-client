package com.forgestorm.client.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class AntiBleedOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {

    public AntiBleedOrthogonalTiledMapRenderer(TiledMap map, SpriteBatch spriteBatch) {
        super(fixTilesPixelBleeding(map), spriteBatch);
    }

    private static TiledMap fixTilesPixelBleeding(TiledMap tiledMap) {
        for (MapLayer layer : tiledMap.getLayers()) {
            // Hide collision layers if they are not hidden.
            if (layer.getName().equals("collision")) layer.setVisible(false);
            if (!layer.isVisible()) continue;
            if (layer instanceof TiledMapTileLayer) fixTilePixelBleeding((TiledMapTileLayer) layer);
        }
        return tiledMap;
    }

    private static void fixTilePixelBleeding(TiledMapTileLayer layer) {
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    fixPixelBleeding(cell.getTile().getTextureRegion());
                }
            }
        }
    }

    private static void fixPixelBleeding(TextureRegion region) {
        float fix = 0.01f;

        float x = region.getRegionX();
        float y = region.getRegionY();
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float invTexWidth = 1f / region.getTexture().getWidth();
        float invTexHeight = 1f / region.getTexture().getHeight();

        region.setRegion(
                (x + fix) * invTexWidth,
                (y + fix) * invTexHeight,
                (x + width - fix) * invTexWidth,
                (y + height - fix) * invTexHeight
        );
    }
}
