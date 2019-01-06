package com.valenguard.client.game.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.Entity;

import lombok.Getter;

public class AttachableCamera extends OrthographicCamera {

    @Getter
    private float lastZoomLevel;
    private Entity following;

    AttachableCamera(float width, float height, float zoom) {
        super.setToOrtho(false, width, height);
        super.update();
        this.zoom = zoom;
        this.lastZoomLevel = zoom;
    }

    public void attachEntity(Entity following) {
        this.following = following;
    }

    void clampCamera(Viewport screenViewport, TiledMap tiledMap) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        float cameraMinX = (screenViewport.getScreenWidth() / 2) * zoom;
        float cameraMinY = (screenViewport.getScreenHeight() / 2) * zoom;
        float cameraMaxX = layer.getWidth() * layer.getTileWidth() - cameraMinX;
        float cameraMaxY = layer.getHeight() * layer.getTileHeight() - cameraMinY;

        position.x = MathUtils.clamp(following.getDrawX() + (ClientConstants.TILE_SIZE / 2), cameraMinX, cameraMaxX);
        position.y = MathUtils.clamp(following.getDrawY() + (ClientConstants.TILE_SIZE / 2), cameraMinY, cameraMaxY);
    }

    public void scrollZoomLevel(double amount) {
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
        lastZoomLevel = (float) change;
        Valenguard.getInstance().getStageHandler().getMainSettingsWindow().getGraphicsTab().setZoomLevel(zoom);
    }

    public void changeZoomLevel(float amount) {
        zoom = amount;
        lastZoomLevel = amount;
        Valenguard.getInstance().getStageHandler().getMainSettingsWindow().getGraphicsTab().setZoomLevel(zoom);
    }

    public Vector3 unprojectCamera(final float screenX, final float screenY) {
        return unproject(new Vector3(screenX, screenY, 0));
    }
}