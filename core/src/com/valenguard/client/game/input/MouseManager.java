package com.valenguard.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.entities.StationaryEntity;
import com.valenguard.client.game.maps.MapUtil;
import com.valenguard.client.game.maps.data.CursorDrawType;
import com.valenguard.client.game.maps.data.GameMap;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.InputData;
import com.valenguard.client.game.movement.MoveUtil;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.network.packet.out.ClickActionPacketOut;
import com.valenguard.client.util.FadeOut;
import com.valenguard.client.util.MoveNode;
import com.valenguard.client.util.PathFinding;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

public class MouseManager {

    private static final boolean PRINT_DEBUG = false;

    public static final int NUM_TICKS_TO_FADE_MOUSE = 60;

    private final PathFinding pathFinding = new PathFinding();

    private final Vector3 clickLocation = new Vector3();
    @Getter
    private int leftClickTileX, leftClickTileY;
    @Getter
    private int rightClickTileX, rightClickTileY;
    @Getter
    private int mouseTileX, mouseTileY;
    private float mouseWorldX, mouseWorldY;

    @Setter
    private boolean invalidate = true;

    private Timer.Task waitForMouseFadeTask;

    @Getter
    private FadeOut fadeOut = new FadeOut();

    void mouseMove(final int screenX, final int screenY) {
        final Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.mouseTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
        this.mouseWorldX = tiledMapCoordinates.x;
        this.mouseWorldY = tiledMapCoordinates.y;

        if (waitForMouseFadeTask != null) {
            waitForMouseFadeTask.cancel();
        }

        fadeOut.cancelFade();

        waitForMouseFadeTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!fadeOut.isFading()) {
                    fadeOut.startFade(NUM_TICKS_TO_FADE_MOUSE);
                }
            }
        }, 2);
    }

    void mouseClick(final int screenX, final int screenY, final int button) {

        if (invalidate) return;

        if (button == Input.Buttons.LEFT) left(screenX, screenY);
        else if (button == Input.Buttons.MIDDLE) middle(screenX, screenY);
        else if (button == Input.Buttons.RIGHT) right(screenX, screenY);
        else if (button == Input.Buttons.FORWARD) forward(screenX, screenY);
        else if (button == Input.Buttons.BACK) back(screenX, screenY);
    }

    private Vector3 cameraXYtoTiledMapXY(final int screenX, final int screenY) {
        return Valenguard.gameScreen.getCamera().unproject(clickLocation.set(screenX, screenY, 0));
    }

    private boolean entityClickTest(float drawX, float drawY) {
        if (mouseWorldX >= drawX && mouseWorldX < drawX + 16) {
            return mouseWorldY >= drawY && mouseWorldY < drawY + 16;
        }
        return false;
    }

    private void left(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.leftClickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.leftClickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        int playerTileX = playerClient.getCurrentMapLocation().getX();
        int playerTileY = playerClient.getCurrentMapLocation().getY();
        Location clientLocation = playerClient.getFutureMapLocation();


        Queue<MoveNode> moveNodes = null;
        for (MovingEntity movingEntity : EntityManager.getInstance().getMovingEntityList().values()) {
            if (entityClickTest(movingEntity.getDrawX(), movingEntity.getDrawY())) {

                // New Entity click so lets cancelTracking entityTracker
                Valenguard.getInstance().getEntityTracker().cancelTracking();

                Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), leftClickTileX, leftClickTileY, clientLocation.getMapName(), false);
                if (testMoveNodes == null) break;

                moveNodes = new LinkedList<MoveNode>();
                for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                    moveNodes.add(testMoveNodes.remove());
                }

                Valenguard.getInstance().getEntityTracker().startTracking(movingEntity);
                println(getClass(), "Interacting with moving entity");
                break;
            }
        }

        // Skill nodes like Mining and Fishing etc
        for (StationaryEntity stationaryEntity : EntityManager.getInstance().getStationaryEntityList().values()) {
            if (entityClickTest(stationaryEntity.getDrawX(), stationaryEntity.getDrawY())) {
                Location location = stationaryEntity.getCurrentMapLocation();

                if ((playerTileX - 1 == location.getX() && playerTileY == location.getY()) ||
                        (playerTileX + 1 == location.getX() && playerTileY == location.getY()) ||
                        (playerTileX == location.getX() && playerTileY - 1 == location.getY()) ||
                        (playerTileX == location.getX() && playerTileY + 1 == location.getY())) {
                    // The player is requesting to interact with the entity.
                    if (!MoveUtil.isEntityMoving(playerClient)) {
                        new ClickActionPacketOut(new ClickAction(ClickAction.LEFT, stationaryEntity)).sendPacket();
                    }
                } else {
                    // New Entity click so lets cancelTracking entityTracker
                    Valenguard.getInstance().getEntityTracker().cancelTracking();

                    // Top right quad
                    Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), leftClickTileX, leftClickTileY, clientLocation.getMapName(), true);
                    if (testMoveNodes == null) break;
                    moveNodes = new LinkedList<MoveNode>();
                    for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                        moveNodes.add(testMoveNodes.remove());
                    }

                }
                break;
            }
        }

        // Click to walk path finding
        if (moveNodes == null) {
            // New Entity click so lets cancelTracking entityTracker
            Valenguard.getInstance().getEntityTracker().cancelTracking();
            moveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), leftClickTileX, leftClickTileY, clientLocation.getMapName(), false);
        }

        if (moveNodes == null) return;

        Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes));
    }

    private void middle(final int screenX, final int screenY) {
        println(getClass(), "Middle Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private void right(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.rightClickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.rightClickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        int playerTileX = playerClient.getCurrentMapLocation().getX();
        int playerTileY = playerClient.getCurrentMapLocation().getY();

        for (StationaryEntity stationaryEntity : EntityManager.getInstance().getStationaryEntityList().values()) {
            if (entityClickTest(stationaryEntity.getDrawX(), stationaryEntity.getDrawY())) {
                Location location = stationaryEntity.getCurrentMapLocation();

                if (!MoveUtil.isEntityMoving(playerClient)) {
                    if ((playerTileX - 1 == location.getX() && playerTileY == location.getY()) ||
                            (playerTileX + 1 == location.getX() && playerTileY == location.getY()) ||
                            (playerTileX == location.getX() && playerTileY - 1 == location.getY()) ||
                            (playerTileX == location.getX() && playerTileY + 1 == location.getY())) {
                        // The player is requesting to interact with the entity.
                        System.out.println("Interacting with entity");
                        new ClickActionPacketOut(new ClickAction(ClickAction.RIGHT, stationaryEntity)).sendPacket();
                    }
                }
            }
        }
    }

    private void forward(final int screenX, final int screenY) {
        println(getClass(), "Forward Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private void back(final int screenX, final int screenY) {
        println(getClass(), "Back Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    public void drawMovingMouse(PlayerClient playerClient, SpriteBatch spriteBatch) {
        GameMap gameMap = playerClient.getGameMap();
        if (MapUtil.isOutOfBounds(gameMap, mouseTileX, mouseTileY)) return;
        CursorDrawType cursorDrawType = gameMap.getMap()[mouseTileX][mouseTileY].getCursorDrawType();
        if (cursorDrawType == CursorDrawType.NO_DRAWABLE) return;
        spriteBatch.begin();
        fadeOut.draw(spriteBatch,
                new ImageBuilder(GameAtlas.CURSOR, cursorDrawType.getDrawableRegion(), cursorDrawType.getSize()).buildTextureRegionDrawable(),
                mouseWorldX - (cursorDrawType.getSize() / 2),
                mouseWorldY - (cursorDrawType.getSize() / 2),
                cursorDrawType.getSize(),
                cursorDrawType.getSize());
        spriteBatch.end();
    }

    public void drawMoveNodes(SpriteBatch spriteBatch) {
        if (Valenguard.getInstance().getClientMovementProcessor().getCurrentMovementInput() == ClientMovementProcessor.MovementInput.MOUSE) {
            Queue<MoveNode> remainingMoveNodes = Valenguard.getInstance().getClientPlayerMovementManager().getMovements();
            for (MoveNode moveNode : remainingMoveNodes) {
                spriteBatch.draw(new ImageBuilder(GameAtlas.CURSOR, "path_find").buildTextureRegionDrawable().getRegion(), moveNode.getWorldX() * ClientConstants.TILE_SIZE, moveNode.getWorldY() * ClientConstants.TILE_SIZE);
            }
        }
    }

    public void invalidateMouse() {
        invalidate = true;
    }
}
