package com.valenguard.client.game.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.entities.Entity;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.constant.ScreenResolutions;

import lombok.Getter;

public class AttachableCamera extends OrthographicCamera {

    @Getter
    private float lastZoomLevel;
    private Entity following;

    AttachableCamera(ScreenResolutions screenResolutions, float zoom) {
        super.setToOrtho(false, screenResolutions.getWidth(), screenResolutions.getHeight());
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

    public void scrollZoomLevel(float amount) {
        float change = zoom + amount;

        if (change <= ClientConstants.ZOOM_LIMIT_IN) {
            change = ClientConstants.ZOOM_LIMIT_IN;
        } else if (change >= ClientConstants.ZOOM_LIMIT_OUT) {
            change = ClientConstants.ZOOM_LIMIT_OUT;
        }

        zoom = change;
        lastZoomLevel = change;
        ActorUtil.getStageHandler().getMainSettingsWindow().getGraphicsTab().setZoomLevel(zoom);
    }

    public void changeZoomLevel(float amount) {
        zoom = amount;
        lastZoomLevel = amount;
        ActorUtil.getStageHandler().getMainSettingsWindow().getGraphicsTab().setZoomLevel(zoom);
    }

    public Vector3 unprojectCamera(final float screenX, final float screenY) {
        return unproject(new Vector3(screenX, screenY, 0));
    }
}
