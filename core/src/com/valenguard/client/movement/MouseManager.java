package com.valenguard.client.movement;

import com.badlogic.gdx.math.Vector3;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.List;

import lombok.Getter;

@Getter
public class MouseManager {

    private final PathFinding pathFinding = new PathFinding();
    private List<PathFinding.MoveNode> moveNodes;
    private Vector3 clickLocation = new Vector3();
    private int clickTileX, clickTileY;
    private int mouseTileX, mouseTileY;

    public boolean mouseMove(int screenX, int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.mouseTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
        return true;
    }

    public boolean mouseClick(int screenX, int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.clickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.clickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        moveNodes = pathFinding.findPath(playerClient.getCurrentMapLocation().getX(), playerClient.getCurrentMapLocation().getY(), clickTileX, clickTileY);

        if (moveNodes == null) return false;
        // TODO: Toggle a UI or Entity click event
        System.out.println("TODO: Toggle a UI or Entity click event");
        return true;
    }

    private Vector3 cameraXYtoTiledMapXY(int screenX, int screenY) {
        return Valenguard.gameScreen.getCamera().unproject(clickLocation.set(screenX, screenY, 0));
    }
}
