package com.valenguard.client.game.maps;

import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.Log;

import lombok.Getter;

public class MapRenderer implements Disposable {

    private static final boolean PRINT_DEBUG = false;

    @Getter
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    @Getter
    private String gameMapNameFromServer;

    public boolean isReadyToRender() {
        return orthogonalTiledMapRenderer != null && tiledMap != null;
    }

    public void renderBottomMapLayers(AttachableCamera camera) {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalTiledMapRenderer.getBatch().begin();
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer.getName().equals("overhead")) continue;
            renderLayer(layer);
        }
        orthogonalTiledMapRenderer.getBatch().end();
    }

    private void renderLayer(MapLayer layer) {
        if (!layer.isVisible()) return;
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
        orthogonalTiledMapRenderer.getBatch().begin();
        orthogonalTiledMapRenderer.renderTileLayer((TiledMapTileLayer) tiledMap.getLayers().get("overhead"));
        orthogonalTiledMapRenderer.getBatch().end();
    }

    /**
     * Sets the tiled map to be rendered.
     *
     * @param mapName The tiled map based on name
     */
    public void setTiledMap(String mapName) {
        gameMapNameFromServer = mapName;
        String filePath = ClientConstants.MAP_DIRECTORY + "/" + mapName + ".tmx";
        Log.println(getClass(), "Map Path: " + filePath, false, PRINT_DEBUG);
        Log.println(getClass(), "Map Name: " + mapName, false, PRINT_DEBUG);
        Valenguard.getInstance().getFileManager().loadTiledMap(filePath);
        tiledMap = Valenguard.getInstance().getFileManager().getTiledMap(filePath);

        if (orthogonalTiledMapRenderer == null)
            orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        else
            orthogonalTiledMapRenderer.setMap(Valenguard.getInstance().getFileManager().getTiledMap(filePath));
    }

    @Override
    public void dispose() {
        if (orthogonalTiledMapRenderer != null) orthogonalTiledMapRenderer.dispose();
    }
}