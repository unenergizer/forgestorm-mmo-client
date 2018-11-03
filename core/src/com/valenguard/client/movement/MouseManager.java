package com.valenguard.client.movement;

import com.badlogic.gdx.math.Vector3;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.MoveDirection;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MouseManager {

    private final PathFinding pathFinding = new PathFinding();

    @Setter
    private Queue<PathFinding.MoveNode> moveNodes;

    private Vector3 clickLocation = new Vector3();
    private int clickTileX, clickTileY;
    private int mouseTileX, mouseTileY;

    public void mouseMove(int screenX, int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.mouseTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
    }

    public void mouseClick(int screenX, int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.clickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.clickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Queue<PathFinding.MoveNode> testMoveNodes = pathFinding.findPath(playerClient.getFutureMapLocation().getX(), playerClient.getFutureMapLocation().getY(), clickTileX, clickTileY);

        if (testMoveNodes == null) return;
        // TODO: Toggle a UI or Entity click event
        System.out.println("TODO: Toggle a UI or Entity click event");

        if (moveNodes != null) {
            Valenguard.getInstance().getClientPlayerMovementManager().setProcessingNodes(false);
        }

        moveNodes = testMoveNodes;

        // Canceling predictions for movement.
        playerClient.setPredictedMoveDirection(MoveDirection.NONE);
    }

    private Vector3 cameraXYtoTiledMapXY(int screenX, int screenY) {
        return Valenguard.gameScreen.getCamera().unproject(clickLocation.set(screenX, screenY, 0));
    }
}
