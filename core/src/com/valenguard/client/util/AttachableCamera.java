package com.valenguard.client.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valenguard.client.constants.ClientConstants;
import com.valenguard.client.entities.Entity;

public class AttachableCamera extends OrthographicCamera {

    private Entity following;

    public AttachableCamera(float width, float height, float zoom) {
        super.setToOrtho(false, width, height);
        super.update();
        this.zoom = zoom;
    }

    public void attachEntity(Entity following) {
        this.following = following;
    }

    public void clampCamera(Viewport screenViewport, TiledMap tiledMap) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        float cameraMinX = (screenViewport.getScreenWidth() / 2) * zoom;
        float cameraMinY = (screenViewport.getScreenHeight() / 2) * zoom;
        float cameraMaxX = layer.getWidth() * layer.getTileWidth() - cameraMinX;
        float cameraMaxY = layer.getHeight() * layer.getTileHeight() - cameraMinY;

        position.x = MathUtils.clamp(following.getDrawX() + (ClientConstants.TILE_SIZE / 2), cameraMinX, cameraMaxX);
        position.y = MathUtils.clamp(following.getDrawY() + (ClientConstants.TILE_SIZE / 2), cameraMinY, cameraMaxY);
    }

    public void changeZoomLevel(double amount) {
        final double zoomLimitIn = ClientConstants.ZOOM_LIMIT_IN;
        final double zoomLimitOut = ClientConstants.ZOOM_LIMIT_OUT;
        double current = zoom * 10;
        double change = (current + amount) / 10;

        if (change <= zoomLimitIn) {
            change = zoomLimitIn;
        } else if (change >= zoomLimitOut) {
            change = zoomLimitOut;
        }

        zoom = (float) change;
    }
}
