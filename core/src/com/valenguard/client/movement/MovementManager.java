package com.valenguard.client.movement;

import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.Valenguard;
import com.valenguard.client.constants.ClientConstants;
import com.valenguard.client.constants.Direction;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.maps.MapUtil;
import com.valenguard.client.maps.data.TmxMap;
import com.valenguard.client.maps.data.Warp;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.List;

import lombok.Getter;

public class MovementManager {

    private PathFinding pathFinding = new PathFinding();

    private static final float MAX_TIME = .5f;
    private float walkTime;
    private boolean walking = false;

    @Getter // Live mouse movement updates
    private int mouseTileX, mouseTileY;
    @Getter
    private int clickTileX, clickTileY;
    @Getter
    private List<PathFinding.MoveNode> moveNodes;

    public void mouseMove(int x, int y) {
        this.mouseTileX = x;
        this.mouseTileY = y;
    }

    public void mouseClick(int x, int y) {
        this.clickTileX = x;
        this.clickTileY = y;

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        moveNodes = pathFinding.findPath(playerClient.getCurrentMapLocation().getX(), playerClient.getCurrentMapLocation().getY(), x, y);

        if (moveNodes == null) return;
    }

    public void playerMove(PlayerClient playerClient, int amountX, int amountY, Direction direction) {
        if (walking) return;

        TmxMap tmxMap = Valenguard.getInstance().getMapManager().getTmxMap(playerClient.getMapName());
        int currentX = playerClient.getCurrentMapLocation().getX();
        int currentY = playerClient.getCurrentMapLocation().getY();

        if (!MapUtil.isTraversable(tmxMap, currentX + amountX, currentY + amountY)) {
            // Play sound or something
            System.out.println("Collided with wall");
            return;
        }

        if (MapUtil.isOutOfBounds(tmxMap, currentX + amountX, currentY + amountY)) {
            // Play sound or something
            System.out.println("Tried to playerMove out of bounds");
            return;
        }

        if (MapUtil.hasWarp(tmxMap, currentX + amountX, currentY + amountY)) {
            // Play sound or something
            Warp warp = MapUtil.getWarp(tmxMap, currentX + amountX, currentY + amountY);
            System.out.println("[WARP] Map: " + warp.getDestinationMapName()
                    + ", FaceDirection: " + warp.getDirectionToFace().toString()
                    + ", X: " + warp.getX()
                    + ", Y: " + warp.getY());
        }

        // Setting the player's direction for movement.
        playerClient.setMoveDirection(direction);

        // TODO: Send move packet
//        new PlayerMove(Direction.RIGHT).sendPacket();

        playerClient.getFutureMapLocation().add(amountX, amountY);
        walking = true;
    }

    private void predictFutureMovement() {

    }

    private void createNewMovement() {

    }

    private void finishPlayerMove() {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        playerClient.setDrawX(playerClient.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(playerClient.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);
        walkTime = 0;
        walking = false;
    }

    public void tick(float delta) {
        if (walking) {
            walkTime += delta;
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

            int currentX = playerClient.getCurrentMapLocation().getX();
            int currentY = playerClient.getCurrentMapLocation().getY();

            int futureX = playerClient.getFutureMapLocation().getX();
            int futureY = playerClient.getFutureMapLocation().getY();

            playerClient.setDrawX(Interpolation.linear.apply(currentX, futureX, walkTime / MAX_TIME) * ClientConstants.TILE_SIZE);
            playerClient.setDrawY(Interpolation.linear.apply(currentY, futureY, walkTime / MAX_TIME) * ClientConstants.TILE_SIZE);


            if (walkTime > MAX_TIME) {
                if (playerClient.getFutureDirection() != Direction.STOP) {
                    // Change player directional information for continuing movement.
                    playerClient.setCurrentMapLocation(playerClient.getFutureMapLocation());
                    playerClient.setMoveDirection(playerClient.getFutureDirection());
                    walkTime = 0;
                } else {
                    finishPlayerMove();
                }

            }
        }
    }

}
