package com.forgestorm.client.game.world.maps.building;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.TilePropertiesLoader;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.WorldBuilderPacketOut;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
public class WorldBuilder {

    private Map<Integer, TileImage> tileImageMap;
    private TextureAtlas textureAtlas;
    private Array<TextureAtlas.AtlasRegion> regions;

    @Setter
    private LayerDefinition currentLayer = LayerDefinition.ROOF;
    @Setter
    private int currentTextureId = 0;


    public WorldBuilder() {
        // Load CustomTileProperties.yaml
        TilePropertiesLoader tilePropertiesLoader = new TilePropertiesLoader();
        tileImageMap = tilePropertiesLoader.loadTileProperties();

        // Load Tiles atlas
        FileManager fileManager = ClientMain.getInstance().getFileManager();
        fileManager.loadAtlas(GameAtlas.TILES);
        textureAtlas = fileManager.getAtlas(GameAtlas.TILES);
        regions = textureAtlas.getRegions();

    }

    public void placeTile(int tileX, int tileY) {
        // Only allow tile place if the World Builder is open
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;
        placeTile(currentLayer, currentTextureId, tileX, tileY);
        new WorldBuilderPacketOut(currentLayer, currentTextureId, (short) tileX, (short) tileY).sendPacket();
    }

    public void placeTile(LayerDefinition layerDefinition, int textureId, int tileX, int tileY) {
        TiledMap tiledMap = ClientMain.getInstance().getGameScreen().getMapRenderer().getTiledMap();
        MapLayers layers = tiledMap.getLayers();
        TiledMapTileLayer layer = (TiledMapTileLayer) layers.get(layerDefinition.getLayerName());
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);

        if (cell == null) {
            println(getClass(), "Cell was null, creating a new one!");
            cell = new TiledMapTileLayer.Cell();
        } else {
            println(getClass(), "Cell found!");
        }

        println(getClass(), "TiledMapLayer: " + layer.getName() + ", Width: " + layer.getWidth() + ", Height: " + layer.getHeight());
        println(getClass(), "Cell X: " + tileX + ", Cell Y: " + tileY);
        TextureRegion textureRegion = textureAtlas.findRegion(tileImageMap.get(textureId).getFileName());

        if (cell.getTile() == null) {
            StaticTiledMapTile tiledMapTile = new StaticTiledMapTile(textureRegion);
            cell.setTile(tiledMapTile);
            layer.setCell(tileX, tileY, cell);
        } else {
            cell.getTile().setTextureRegion(textureRegion);
        }
    }

    public void drawMouse(SpriteBatch spriteBatch) {
        if (!ClientMain.getInstance().getStageHandler().getTileBuildMenu().isVisible()) return;
        MouseManager mouseManager = ClientMain.getInstance().getMouseManager();
        int x = mouseManager.getMouseTileX() * 16;
        int y = mouseManager.getMouseTileY() * 16;

        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(tileImageMap.get(currentTextureId).getFileName());
        spriteBatch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    public void addNewTile(TileImage newTileImage) {
        // Search for existing entry...
        for (TileImage tileImage : tileImageMap.values()) {
            if (tileImage.getFileName().equals(newTileImage.getFileName())) {
                tileImage.setBuildCategory(newTileImage.getBuildCategory());

                if (newTileImage.getLayerDefinition() != null) {
                    tileImage.setLayerDefinition(newTileImage.getLayerDefinition());
                }

                if (newTileImage.getCustomTileProperties() != null) {
                    tileImage.setCustomTileProperties(newTileImage.getCustomTileProperties());
                }

                return;
            }
        }

        // if no entry found, create a new one
        tileImageMap.put(newTileImage.getImageId(), newTileImage);
    }
}
