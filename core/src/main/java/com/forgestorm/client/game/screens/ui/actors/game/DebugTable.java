package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class DebugTable extends VisTable implements Buildable {

    private final ClientMain clientMain;
    private final VisLabel fps = new VisLabel();
    private final VisLabel connection = new VisLabel();
    private final VisLabel playerWorld = new VisLabel();
    private final VisLabel playerChunk = new VisLabel();
    private final VisLabel playerTile = new VisLabel();
    private final VisLabel cursorTile = new VisLabel();
    private final VisLabel health = new VisLabel();
    private final VisLabel armor = new VisLabel();
    private final VisLabel damage = new VisLabel();

    private StageHandler stageHandler;

    public DebugTable(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;

        add(fps).left().row();
        add(connection).left().row();
        add(playerWorld).left().row();
        add(playerTile).left().row();
        add(playerChunk).left().row();
        add(cursorTile).left().row();
        add(health).left().row();
        add(armor).left().row();
        add(damage).left().row();

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        pack();
        findPosition();
        setVisible(false);
        return this;
    }

    public void findPosition() {
        UserInterfaceType userInterfaceType = clientMain.getUserInterfaceType();

        if (userInterfaceType == null) return;

        switch (userInterfaceType) {
            case LOGIN:
                setPosition(StageHandler.WINDOW_PAD_X, Gdx.graphics.getHeight() - getHeight() - StageHandler.WINDOW_PAD_Y);
                break;
            case CHARACTER_SELECT:
                float xAdd = stageHandler.getCharacterSelectMenu().getWidth();
                setPosition(StageHandler.WINDOW_PAD_X + xAdd, Gdx.graphics.getHeight() - getHeight() - StageHandler.WINDOW_PAD_Y);
                break;
            case GAME:
                StatusBar statusBar = stageHandler.getStatusBar();
                setPosition(StageHandler.WINDOW_PAD_X, statusBar.getY() - getHeight() - StageHandler.WINDOW_PAD_Y);
                break;
        }

        // Reset text for good measure
        for (Actor actor : this.getChildren()) {
            if (actor instanceof VisLabel) ((VisLabel) actor).setText("");
        }
    }

    public void refresh(float delta) {
        if (!isVisible()) return;

        final PlayerClient playerClient = clientMain.getEntityManager().getPlayerClient();
        final MouseManager mouseManager = clientMain.getMouseManager();

        if (clientMain.getUserInterfaceType() != null && clientMain.getUserInterfaceType() == UserInterfaceType.GAME) {
            fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + ", Zoom: " + clientMain.getGameScreen().getCamera().zoom + ", Delta: " + Math.round(delta * 100000.0) / 100000.0);
        } else {
            fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + ", Delta: " + Math.round(delta * 100000.0) / 100000.0);
        }

        if (playerClient != null) {
            connection.setText("UUID: " + playerClient.getServerEntityID() + ", MS: " + clientMain.getConnectionManager().getClientGameConnection().getClientHandler().getClientPing());

            int x = playerClient.getCurrentMapLocation().getX();
            int y = playerClient.getCurrentMapLocation().getY();
            short z = playerClient.getCurrentMapLocation().getZ();

            playerWorld.setText("World: " + playerClient.getCurrentMapLocation().getWorldName() + ", FacingDirection: " + playerClient.getFacingDirection());
            playerTile.setText("Tile XYZ: " + x + " / " + y + " / " + z + ", Pixel XY: " + playerClient.getDrawX() + " / " + playerClient.getDrawY());

            GameWorld gameWorld = clientMain.getWorldManager().getCurrentGameWorld();
            WorldChunk worldChunk = gameWorld.findChunk(x, y);
            if (worldChunk != null) {
                playerChunk.setText("Chunk XY: " + worldChunk.getChunkX() + " / " + worldChunk.getChunkY()
                        + ", WarpCount: " + worldChunk.getNumberOfWarps());
            } else {
                playerChunk.setText("[RED]Chunk is null");
            }

            armor.setText("Armor: " + playerClient.getAttributes().getArmor());
            damage.setText("Damage: " + playerClient.getAttributes().getDamage());
        }

        if (mouseManager != null && clientMain.getUserInterfaceType() == UserInterfaceType.GAME) {
            cursorTile.setText("Mouse XY: " + mouseManager.getMouseTileX() + " / " + mouseManager.getMouseTileY()
                    + ", LeftPress XY: " + mouseManager.getLeftClickTileX() + " / " + mouseManager.getLeftClickTileY()
                    + ", RightPress XY: " + mouseManager.getRightClickTileX() + " / " + mouseManager.getRightClickTileY());
        }

        pack();
    }


}
