package com.valenguard.client.screens.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;

public class GameScreenDebugText implements AbstractUI, Refreshable {

    private static final boolean DEBUG_STAGE = false;

    @Override
    public void build(UiManager uiManager) {

        PlayerClient client = EntityManager.getInstance().getPlayerClient();

        Table wrapperTable = new Table();
        wrapperTable.setFillParent(true);
        wrapperTable.setDebug(DEBUG_STAGE);
        uiManager.stage.addActor(wrapperTable);

        Table infoTable = new Table();
        infoTable.setFillParent(false);
        infoTable.setDebug(DEBUG_STAGE);
        uiManager.stage.addActor(infoTable);

        // create version widgets
        Label delta = new Label("DeltaTime: " + Math.round(Gdx.graphics.getDeltaTime() * 100000.0) / 100000.0, uiManager.skin);
        Label fps = new Label("FPS: " + Gdx.graphics.getFramesPerSecond(), uiManager.skin);
        Label ms = new Label("MS: " + Valenguard.getInstance().getPingManager().getLatency(), uiManager.skin);

        wrapperTable.add(infoTable).expand().left().top().pad(10);

        // init client version in lower left hand corner
        infoTable.add(delta).left();
        infoTable.row();
        infoTable.add(fps).left();
        infoTable.row();
        infoTable.add(ms).left();
        infoTable.row();

        if (client != null && client.getCurrentMapLocation() != null) {
            Label uuid, playerTile, playerPixel, zoom, cursorTile, cursorTileClick;
            uuid = new Label("UUID: " + client.getServerEntityID(), uiManager.skin);
            playerTile = new Label("Player X: " + client.getCurrentMapLocation().getX() + ", Y: " + client.getCurrentMapLocation().getY() + ", map: " + client.getCurrentMapLocation().getMapName(), uiManager.skin);
            playerPixel = new Label("Player X: " + client.getDrawX() + ", Y: " + client.getDrawY(), uiManager.skin);
            cursorTileClick = new Label("Click X: " + Valenguard.getInstance().getMouseManager().getClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getClickTileY(), uiManager.skin);
            cursorTile = new Label("Cursor X: " + Valenguard.getInstance().getMouseManager().getMouseTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getMouseTileY(), uiManager.skin);
            zoom = new Label("Zoom: " + Valenguard.gameScreen.getCamera().zoom, uiManager.skin);

            infoTable.add(uuid).left();
            infoTable.row();
            infoTable.add(playerPixel).left();
            infoTable.row();
            infoTable.add(playerTile).left();
            infoTable.row();
            infoTable.add(cursorTile).left();
            infoTable.row();
            infoTable.add(cursorTileClick).left();
            infoTable.row();
            infoTable.add(zoom).left();
        }
    }
}
