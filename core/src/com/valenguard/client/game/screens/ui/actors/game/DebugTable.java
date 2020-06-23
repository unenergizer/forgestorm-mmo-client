package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.ClientMain;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.screens.UserInterfaceType;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.PlayerClient;

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
    private final VisLabel health = new VisLabel();
    private final VisLabel armor = new VisLabel();
    private final VisLabel damage = new VisLabel();

    @Override
    public Actor build(final StageHandler stageHandler) {
        add(delta).left().row();
        add(fps).left().row();
        add(zoom).left().row();
        add(ms).left().row();
        add(uuid).left().row();
        add(playerPixel).left().row();
        add(playerTile).left().row();
        add(cursorTile).left().row();
        add(leftCursorTileClick).left().row();
        add(rightCursorTileClick).left().row();
        add(health).left().row();
        add(armor).left().row();
        add(damage).left().row();

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

        final PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        final MouseManager mouseManager = ClientMain.getInstance().getMouseManager();

        this.delta.setText("DeltaTime: " + Math.round(delta * 100000.0) / 100000.0);
        fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        if (ClientMain.gameScreen.getCamera() != null) {
            zoom.setText("Zoom: " + ClientMain.gameScreen.getCamera().zoom);
        }

        if (ClientMain.connectionManager.getClientGameConnection().isConnected()) {
            ms.setText("MS: " + ClientMain.connectionManager.getClientGameConnection().getPing());
        } else {
            ms.setText("MS: Not connected to server.");
        }

        if (playerClient != null) {
            uuid.setText("UUID: " + playerClient.getServerEntityID());
            playerTile.setText("Player X: " + Math.round(playerClient.getCurrentMapLocation().getX()) + ", Y: " + Math.round(playerClient.getCurrentMapLocation().getY()) + ", map: " + playerClient.getCurrentMapLocation().getMapName());
            playerPixel.setText("Player X: " + playerClient.getDrawX() + ", Y: " + playerClient.getDrawY());

            armor.setText("Armor: " + playerClient.getAttributes().getArmor());
            damage.setText("Damage: " + playerClient.getAttributes().getDamage());
        }

        if (mouseManager != null && ClientMain.getInstance().getUserInterfaceType() == UserInterfaceType.GAME) {
            cursorTile.setText("Cursor-Tile X: " + mouseManager.getMouseTileX() + ", Y: " + mouseManager.getMouseTileY());
            leftCursorTileClick.setText("Left-Click X: " + mouseManager.getLeftClickTileX() + ", Y: " + mouseManager.getLeftClickTileY());
            rightCursorTileClick.setText("Right-Click X: " + mouseManager.getRightClickTileX() + ", Y: " + mouseManager.getRightClickTileY());
        }

        pack();
    }


}
