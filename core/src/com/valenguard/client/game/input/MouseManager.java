package com.valenguard.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.movement.InputData;
import com.valenguard.client.util.MoveNode;
import com.valenguard.client.util.PathFinding;

import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MouseManager {

    private final PathFinding pathFinding = new PathFinding();

    private Vector3 clickLocation = new Vector3();
    private int leftClickTileX, leftClickTileY;
    private int rightClickTileX, rightClickTileY;
    private int mouseTileX, mouseTileY;
    private float mouseScreenX, mouseScreenY;

    @Setter
    private boolean invalidate = true;

    public void mouseMove(final int screenX, final int screenY) {
        final Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.mouseTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
        this.mouseScreenX = tiledMapCoordinates.x;
        this.mouseScreenY = tiledMapCoordinates.y;
    }

    public void mouseClick(final int screenX, final int screenY, final int button) {

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

        Valenguard.getInstance().getClientMovementProcessor().preprocessMovement(
                new InputData(
                        ClientMovementProcessor.MovementInput.MOUSE,
                        testMoveNodes));
    }

    private void middle(final int screenX, final int screenY) {
    }

    private void right(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.rightClickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.rightClickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
    }

    private void forward(final int screenX, final int screenY) {
    }

    private void back(final int screenX, final int screenY) {
    }

    public void invalidateMouse() {
        invalidate = true;
    }
}
