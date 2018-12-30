package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.entities.animations.HumanAnimation;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class DebugTable extends VisTable implements Buildable {

    private final VisLabel delta = new VisLabel();
    private final VisLabel fps = new VisLabel();
    private final VisLabel ms = new VisLabel();
    private final VisLabel uuid = new VisLabel();
    private final VisLabel playerTile = new VisLabel();
    private final VisLabel playerPixel = new VisLabel();
    private final VisLabel zoom = new VisLabel();
    private final VisLabel cursorTile = new VisLabel();
    private final VisLabel leftCursorTileClick = new VisLabel();
    private final VisLabel rightCursorTileClick = new VisLabel();
    private final VisLabel bodyParts = new VisLabel();

    @Override
    public Actor build() {
        add(delta).left().row();
        add(fps).left().row();
        add(zoom).left().row();
        add(ms).left().row();
        add(uuid).left().row();
        add(bodyParts).left().row();
        add(playerPixel).left().row();
        add(playerTile).left().row();
        add(cursorTile).left().row();
        add(leftCursorTileClick).left().row();
        add(rightCursorTileClick).left().row();

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(10, Gdx.graphics.getHeight() - getHeight() - 10);
            }
        });

        pack();
        setPosition(10, Gdx.graphics.getHeight() - getHeight() - 10);
        setVisible(false);
        return this;
    }

    public void refresh(float delta) {
        if (!isVisible()) return;

        final PlayerClient client = EntityManager.getInstance().getPlayerClient();
        final MouseManager mouseManager = Valenguard.getInstance().getMouseManager();

        this.delta.setText("DeltaTime: " + Math.round(delta * 100000.0) / 100000.0);
        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        if (Valenguard.gameScreen.getCamera() != null) {
            zoom.setText("Zoom: " + Valenguard.gameScreen.getCamera().zoom);
        }

        if (Valenguard.clientConnection.isConnected()) {
            ms.setText("MS: " + Valenguard.clientConnection.getPing());
        } else {
            ms.setText("MS: Not connected to server.");
        }

        if (client != null) {
            final HumanAnimation humanAnimation = (HumanAnimation) client.getEntityAnimation();
            uuid.setText("UUID: " + client.getServerEntityID());
            bodyParts.setText("HeadID: " + humanAnimation.getHeadId() + ", BodyID: " + humanAnimation.getBodyId());
            playerTile.setText("Player X: " + Math.round(client.getCurrentMapLocation().getX()) + ", Y: " + Math.round(client.getCurrentMapLocation().getY()) + ", map: " + client.getCurrentMapLocation().getMapName());
            playerPixel.setText("Player X: " + client.getDrawX() + ", Y: " + client.getDrawY());
        }

        if (mouseManager != null && Valenguard.getInstance().getScreenType() == ScreenType.GAME) {
            cursorTile.setText("Cursor-Tile X: " + mouseManager.getMouseTileX() + ", Y: " + mouseManager.getMouseTileY());
            leftCursorTileClick.setText("Left-Click X: " + mouseManager.getLeftClickTileX() + ", Y: " + mouseManager.getLeftClickTileY());
            rightCursorTileClick.setText("Right-Click X: " + mouseManager.getRightClickTileX() + ", Y: " + mouseManager.getRightClickTileY());
        }

        pack();
    }


}
