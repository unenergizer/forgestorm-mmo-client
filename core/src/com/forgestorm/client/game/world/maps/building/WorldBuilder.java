package com.forgestorm.client.game.world.maps.building;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.WorldBuilderPacketOut;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@Getter
public class WorldBuilder {

    @Setter
    private LayerDefinition currentLayer = LayerDefinition.ROOF;
    @Setter
    private short currentTextureId = 0;

    public void placeTile(int tileX, int tileY) {
        // Only allow tile place if the World Builder is open
        if (!ClientMain.getInstance().getStageHandler().getWorldBuilderUI().isVisible()) return;
        placeTile(currentLayer, currentTextureId, tileX, tileY);
        new WorldBuilderPacketOut(currentLayer, currentTextureId, (short) tileX, (short) tileY).sendPacket();
    }

    public void placeTile(LayerDefinition layerDefinition, short textureId, int tileX, int tileY) {
        TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);

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
        TextureRegion textureRegion = textureAtlas.findRegion(Short.toString(textureId));

        if (cell.getTile() == null) {
            StaticTiledMapTile tiledMapTile = new StaticTiledMapTile(textureRegion);
            cell.setTile(tiledMapTile);
            layer.setCell(tileX, tileY, cell);
        } else {
            cell.getTile().setTextureRegion(textureRegion);
        }
    }

    public void drawMouse(SpriteBatch spriteBatch) {
        if (!ClientMain.getInstance().getStageHandler().getWorldBuilderUI().isVisible()) return;
        TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
        MouseManager mouseManager = ClientMain.getInstance().getMouseManager();
        int x = mouseManager.getMouseTileX() * 16;
        int y = mouseManager.getMouseTileY() * 16;
        spriteBatch.draw(textureAtlas.findRegion(Short.toString(currentTextureId)), x, y, 16, 16);
    }
}
