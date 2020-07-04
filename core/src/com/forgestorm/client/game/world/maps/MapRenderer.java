package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.AntiBleedOrthogonalTiledMapRenderer;
import com.forgestorm.client.game.screens.AttachableCamera;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.io.FilePaths;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class MapRenderer implements Disposable {

    private static final boolean PRINT_DEBUG = false;

    private final SpriteBatch spriteBatch;

    @Getter
    private TiledMap tiledMap;
    private AntiBleedOrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    @Getter
    private String gameMapNameFromServer;

    public MapRenderer(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    public void setMapColor(float red, float green, float blue, float alpha) {
        if (!isReadyToRender()) return;
        orthogonalTiledMapRenderer.getBatch().setColor(red, green, blue, alpha);
    }

    public void resetMapColor() {
        orthogonalTiledMapRenderer.getBatch().setColor(1, 1, 1, 1);
    }

    public boolean isReadyToRender() {
        return orthogonalTiledMapRenderer != null
                && tiledMap != null
                && ClientMain.getInstance().getUserInterfaceType() == UserInterfaceType.GAME;
    }

    public void renderBottomMapLayers(AttachableCamera camera) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        camera.update();
        orthogonalTiledMapRenderer.setView(camera);
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer.getName().equals("overhead")) continue;
            renderLayer(layer);
        }
    }

    private void renderLayer(MapLayer layer) {
        if (!layer.isVisible()) return;
        if (layer.getName().equalsIgnoreCase("collision")) return;
        if (layer instanceof MapGroupLayer) {
            MapLayers childLayers = ((MapGroupLayer) layer).getLayers();
            for (int i = 0; i < childLayers.size(); i++) {
                MapLayer childLayer = childLayers.get(i);
                if (!childLayer.isVisible()) continue;
                renderLayer(childLayer);
            }
        } else {
            if (layer instanceof TiledMapTileLayer) {
                orthogonalTiledMapRenderer.renderTileLayer((TiledMapTileLayer) layer);
            } else if (layer instanceof TiledMapImageLayer) {
                orthogonalTiledMapRenderer.renderImageLayer((TiledMapImageLayer) layer);
            } else {
                orthogonalTiledMapRenderer.renderObjects(layer);
            }
        }
    }

    public void renderOverheadMapLayers() {
        orthogonalTiledMapRenderer.renderTileLayer((TiledMapTileLayer) tiledMap.getLayers().get("overhead"));
    }

    /**
     * Sets the tiled map to be rendered.
     *
     * @param mapName The tiled map based on name
     */
    public void setTiledMap(String mapName) {
        EntityManager.getInstance().dispose(); // quick clear existing entities
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        if (playerClient != null) playerClient.setTargetEntity(null);
        gameMapNameFromServer = mapName;
        String filePath = FilePaths.MAPS.getFilePath() + "/" + mapName + ".tmx";
        println(getClass(), "Map Path: " + filePath, false, PRINT_DEBUG);
        println(getClass(), "Map Name: " + mapName, false, PRINT_DEBUG);
        ClientMain.getInstance().getFileManager().loadTiledMap(filePath);
        tiledMap = ClientMain.getInstance().getFileManager().getTiledMap(filePath);

        if (orthogonalTiledMapRenderer == null) {
            orthogonalTiledMapRenderer = new AntiBleedOrthogonalTiledMapRenderer(tiledMap, spriteBatch);
        } else {
            orthogonalTiledMapRenderer.setMap(ClientMain.getInstance().getFileManager().getTiledMap(filePath));
        }

        // Map loaded, now fade it in!
        ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);
    }

    @Override
    public void dispose() {
        if (orthogonalTiledMapRenderer != null) orthogonalTiledMapRenderer.dispose();
        if (tiledMap != null) tiledMap.dispose();
    }
}