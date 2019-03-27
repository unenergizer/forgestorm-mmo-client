package com.valenguard.client.game.world.maps;

import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.AttachableCamera;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.io.FilePaths;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

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
        EntityManager.getInstance().dispose(); // quick clear existing entities
        gameMapNameFromServer = mapName;
        String filePath = FilePaths.MAPS.getFilePath() + "/" + mapName + ".tmx";
        println(getClass(), "Map Path: " + filePath, false, PRINT_DEBUG);
        println(getClass(), "Map Name: " + mapName, false, PRINT_DEBUG);
        Valenguard.getInstance().getFileManager().loadTiledMap(filePath);
        tiledMap = Valenguard.getInstance().getFileManager().getTiledMap(filePath);

        if (orthogonalTiledMapRenderer == null) {
            orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        } else {
            orthogonalTiledMapRenderer.setMap(Valenguard.getInstance().getFileManager().getTiledMap(filePath));
        }

        // Map loaded, now fade it in!
        ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);
    }

    @Override
    public void dispose() {
        if (orthogonalTiledMapRenderer != null) orthogonalTiledMapRenderer.dispose();
    }
}
