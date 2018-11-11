package com.valenguard.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.InputData;
import com.valenguard.client.util.Log;
import com.valenguard.client.util.MoveNode;
import com.valenguard.client.util.PathFinding;

import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MouseManager {

    public static final boolean PRINT_DEBUG = false;

    private final PathFinding pathFinding = new PathFinding();

    private Vector3 clickLocation = new Vector3();
    private int leftClickTileX, leftClickTileY;
    private int rightClickTileX, rightClickTileY;
    private int mouseTileX, mouseTileY;
    private float mouseScreenX, mouseScreenY;

    @Setter
    private boolean invalidate = true;

    void mouseMove(final int screenX, final int screenY) {
        final Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.mouseTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
        this.mouseScreenX = tiledMapCoordinates.x;
        this.mouseScreenY = tiledMapCoordinates.y;
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

    private void left(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.leftClickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.leftClickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        // Click to walk path finding
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        Queue<MoveNode> testMoveNodes = pathFinding.findPath(playerClient.getFutureMapLocation().getX(), playerClient.getFutureMapLocation().getY(), leftClickTileX, leftClickTileY, playerClient.getCurrentMapLocation().getMapName());

        if (testMoveNodes == null) return;

        Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                new InputData(ClientMovementProcessor.MovementInput.MOUSE, testMoveNodes));
    }

    private void middle(final int screenX, final int screenY) {
        Log.println(getClass(), "Middle Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private void right(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.rightClickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.rightClickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
    }

    private void forward(final int screenX, final int screenY) {
        Log.println(getClass(), "Forward Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private void back(final int screenX, final int screenY) {
        Log.println(getClass(), "Back Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    public void invalidateMouse() {
        invalidate = true;
    }
}
