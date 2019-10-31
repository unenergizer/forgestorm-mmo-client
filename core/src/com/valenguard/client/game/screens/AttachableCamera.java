package com.valenguard.client.game.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.constant.ScreenResolutions;
import com.valenguard.client.game.world.entities.Entity;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

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

    public static void main(String args[]) {
        test();
    }

    public static void test() {

        for (int def = 0; def < 100; def++) {

            double percentage = (100.0D - (Math.log(def + 1) * 10)) / 100.0D;

            int attackAmount = 10;

            int amountDamaged = (int) (attackAmount * percentage);


            System.out.println("def: " + def + ", amount damaged: " + amountDamaged);

        }

        int amountPlayerIsHurt = getActualDamage(20, 10);


    }

    public static int getActualDamage(int amountAttackedBy, int defense) {
        double percentage = (100.0D - (Math.log(defense + 1) * 10)) / 100.0D;
        int amountDamaged = (int) (amountAttackedBy * percentage);
        return amountDamaged;
    }

    void clampCamera(Viewport screenViewport, TiledMap tiledMap) {
        if (following == null) return;

        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        float cameraMinX = (screenViewport.getScreenWidth() / 2f) * zoom;
        float cameraMinY = (screenViewport.getScreenHeight() / 2f) * zoom;
        float cameraMaxX = layer.getWidth() * layer.getTileWidth() - cameraMinX;
        float cameraMaxY = layer.getHeight() * layer.getTileHeight() - cameraMinY;

        float px = (following.getDrawX() + (ClientConstants.TILE_SIZE / 2f));
        float py = (following.getDrawY() + (ClientConstants.TILE_SIZE / 2f));

        position.x = MathUtils.clamp(px, cameraMinX, cameraMaxX);
        position.y = MathUtils.clamp(py, cameraMinY, cameraMaxY);
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
        println(getClass(), "[Scroll] Zoom: " + zoom + ", Last Zoom: " + lastZoomLevel);
    }

    public void changeZoomLevel(float amount) {
        zoom = amount;
        lastZoomLevel = amount;
        ActorUtil.getStageHandler().getMainSettingsWindow().getGraphicsTab().setZoomLevel(zoom);
        println(getClass(), "[Change] Zoom: " + zoom + ", Last Zoom: " + lastZoomLevel);
    }

    public Vector3 unprojectCamera(final float screenX, final float screenY) {
        return unproject(new Vector3(screenX, screenY, 0));
    }
}
