package com.valenguard.client.game.screens.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;

public class GameScreenDebugText implements com.valenguard.client.game.screens.stage.AbstractUI, com.valenguard.client.game.screens.stage.Refreshable {

    private static final boolean DEBUG_STAGE = false;
    private Label delta, fps, ms, uuid, playerTile, playerPixel, zoom, cursorTile, leftCursorTileClick, rightCursorTileClick;

    @Override
    public void refresh() {

        PlayerClient client = EntityManager.getInstance().getPlayerClient();

        delta.setText("DeltaTime: " + Math.round(Gdx.graphics.getDeltaTime() * 100000.0) / 100000.0);
        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        ms.setText("MS: " + Valenguard.getInstance().getPing());

        if (client != null && client.getCurrentMapLocation() != null) {
            uuid.setText("UUID: " + client.getServerEntityID());
            playerTile.setText("Player X: " + client.getCurrentMapLocation().getX() + ", Y: " + client.getCurrentMapLocation().getY() + ", map: " + client.getCurrentMapLocation().getMapName());
            playerPixel.setText("Player X: " + client.getDrawX() + ", Y: " + client.getDrawY());
            cursorTile.setText("Cursor-Tile X: " + Valenguard.getInstance().getMouseManager().getMouseTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getMouseTileY());
            leftCursorTileClick.setText("LEFT-Click X: " + Valenguard.getInstance().getMouseManager().getLeftClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getLeftClickTileY());
            rightCursorTileClick.setText("RIGHT-Click X: " + Valenguard.getInstance().getMouseManager().getRightClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getRightClickTileY());
            zoom.setText("Zoom: " + Valenguard.gameScreen.getCamera().zoom);
        }
    }

    @Override
    public void build(com.valenguard.client.game.screens.stage.UiManager uiManager) {

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
        delta = new Label("DeltaTime: " + Math.round(Gdx.graphics.getDeltaTime() * 100000.0) / 100000.0, uiManager.skin);
        fps = new Label("FPS: " + Gdx.graphics.getFramesPerSecond(), uiManager.skin);
        ms = new Label("MS: " + Valenguard.getInstance().getPing(), uiManager.skin);

        wrapperTable.add(infoTable).expand().left().top().pad(10);

        infoTable.add(delta).left();
        infoTable.row();
        infoTable.add(fps).left();
        infoTable.row();
        infoTable.add(ms).left();
        infoTable.row();

        if (client != null && client.getCurrentMapLocation() != null) {
            uuid = new Label("UUID: " + client.getServerEntityID(), uiManager.skin);
            playerTile = new Label("Player X: " + client.getCurrentMapLocation().getX() + ", Y: " + client.getCurrentMapLocation().getY() + ", map: " + client.getCurrentMapLocation().getMapName(), uiManager.skin);
            playerPixel = new Label("Player X: " + client.getDrawX() + ", Y: " + client.getDrawY(), uiManager.skin);
            cursorTile = new Label("Cursor-Tile X: " + Valenguard.getInstance().getMouseManager().getMouseTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getMouseTileY(), uiManager.skin);
            leftCursorTileClick = new Label("LEFT-Click X: " + Valenguard.getInstance().getMouseManager().getLeftClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getLeftClickTileY(), uiManager.skin);
            rightCursorTileClick = new Label("RIGHT-Click X: " + Valenguard.getInstance().getMouseManager().getRightClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getRightClickTileY(), uiManager.skin);
            zoom = new Label("Zoom: " + Valenguard.gameScreen.getCamera().zoom, uiManager.skin);

            infoTable.add(uuid).left();
            infoTable.row();
            infoTable.add(playerPixel).left();
            infoTable.row();
            infoTable.add(playerTile).left();
            infoTable.row();
            infoTable.add(cursorTile).left();
            infoTable.row();
            infoTable.add(leftCursorTileClick).left();
            infoTable.row();
            infoTable.add(rightCursorTileClick).left();
            infoTable.row();
            infoTable.add(zoom).left();
        }
    }
}
