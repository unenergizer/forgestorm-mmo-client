package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.entities.animations.HumanAnimation;
import com.valenguard.client.game.screens.ui.actors.Buildable;

public class DebugTable extends VisTable implements Buildable {

    private VisLabel delta, fps, ms, uuid, playerTile, playerPixel, zoom, cursorTile, leftCursorTileClick, rightCursorTileClick, bodyParts;
    private boolean built = false;

    @Override
    public Actor build() {
        built = true;
        PlayerClient client = EntityManager.getInstance().getPlayerClient();
        HumanAnimation humanAnimation = (HumanAnimation) client.getEntityAnimation();

        // create version widgets
        VisTable infoTable = new VisTable();
        delta = new VisLabel("DeltaTime: " + Math.round(Gdx.graphics.getDeltaTime() * 100000.0) / 100000.0);
        fps = new VisLabel("FPS: " + Gdx.graphics.getFramesPerSecond());
        ms = new VisLabel("MS: " + Valenguard.clientConnection.getPing());
        uuid = new VisLabel("UUID: " + client.getServerEntityID());
        playerTile = new VisLabel("Player X: " + client.getCurrentMapLocation().getX() + ", Y: " + client.getCurrentMapLocation().getY() + ", map: " + client.getCurrentMapLocation().getMapName());
        playerPixel = new VisLabel("Player X: " + client.getDrawX() + ", Y: " + client.getDrawY());
        cursorTile = new VisLabel("Cursor-Tile X: " + Valenguard.getInstance().getMouseManager().getMouseTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getMouseTileY());
        leftCursorTileClick = new VisLabel("WEST-Click X: " + Valenguard.getInstance().getMouseManager().getLeftClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getLeftClickTileY());
        rightCursorTileClick = new VisLabel("EAST-Click X: " + Valenguard.getInstance().getMouseManager().getRightClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getRightClickTileY());
        zoom = new VisLabel("Zoom: " + Valenguard.gameScreen.getCamera().zoom);
        bodyParts = new VisLabel("HeadID: " + humanAnimation.getHeadId() + ", BodyID: " + humanAnimation.getBodyId());

        infoTable.add(delta).left();
        infoTable.row();
        infoTable.add(fps).left();
        infoTable.row();
        infoTable.add(ms).left();
        infoTable.row();
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
        infoTable.row();
        infoTable.add(bodyParts).left();

        add(infoTable);
        pack();
        setPosition(10, Gdx.graphics.getHeight() - getHeight() - 10);
        setVisible(false);
        return this;
    }

    public void refresh(float deltaTime) {
        if (!built) return;
        if (!isVisible()) return;
        PlayerClient client = EntityManager.getInstance().getPlayerClient();
        HumanAnimation humanAnimation = (HumanAnimation) client.getEntityAnimation();

        delta.setText("DeltaTime: " + Math.round(deltaTime * 100000.0) / 100000.0);
        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        ms.setText("MS: " + Valenguard.clientConnection.getPing());
        uuid.setText("UUID: " + client.getServerEntityID());
        playerTile.setText("Player X: " + client.getCurrentMapLocation().getX() + ", Y: " + client.getCurrentMapLocation().getY() + ", map: " + client.getCurrentMapLocation().getMapName());
        playerPixel.setText("Player X: " + client.getDrawX() + ", Y: " + client.getDrawY());
        cursorTile.setText("Cursor-Tile X: " + Valenguard.getInstance().getMouseManager().getMouseTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getMouseTileY());
        leftCursorTileClick.setText("WEST-Click X: " + Valenguard.getInstance().getMouseManager().getLeftClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getLeftClickTileY());
        rightCursorTileClick.setText("EAST-Click X: " + Valenguard.getInstance().getMouseManager().getRightClickTileX() + ", Y: " + Valenguard.getInstance().getMouseManager().getRightClickTileY());
        zoom.setText("Zoom: " + Valenguard.gameScreen.getCamera().zoom);
        bodyParts.setText("HeadID: " + humanAnimation.getHeadId() + ", BodyID: " + humanAnimation.getBodyId());

        pack();
    }
}
