package com.valenguard.client.movement;

import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.Direction;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.maps.MapUtil;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.maps.data.TmxMap;
import com.valenguard.client.maps.data.Warp;
import com.valenguard.client.network.packet.out.PlayerMove;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.List;

import lombok.Getter;

public class MovementManager {

    private PathFinding pathFinding = new PathFinding();

    // todo : this number relates to how fast the client moves
    // todo: this should be read in from the server so we will change this at a later date
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

    public boolean playerMove(PlayerClient playerClient, int amountX, int amountY, Direction direction) {

        TmxMap tmxMap = Valenguard.getInstance().getMapManager().getTmxMap(playerClient.getMapName());

        if (walking) {
            return predictFutureMovement(playerClient, tmxMap, amountX, amountY, direction);
        } else {
            return createNewMovement(tmxMap, playerClient, amountX, amountY, direction);
        }
    }

    private boolean predictFutureMovement(PlayerClient playerClient, TmxMap tmxMap, int amountX, int amountY, Direction predictedDirection) {

        Location futureMapLocation = playerClient.getFutureMapLocation();

        // Already predicting that the player will move in that direction.
        if (playerClient.getPredictedDirection() == predictedDirection) {
            return false;
        }

        // The predicted movement is not possible based on input.
        if (!isMovable(tmxMap, futureMapLocation.getX() + amountX, futureMapLocation.getY() + amountY)) {
            return false;
        }

        // Todo: check map warps

        playerClient.setPredictedDirection(predictedDirection);

        // Telling the server where the player plans on moving.
        new PlayerMove(predictedDirection).sendPacket();

        return true;
    }

    private boolean createNewMovement(TmxMap tmxMap, PlayerClient playerClient, int amountX, int amountY, Direction direction) {

        System.err.println("## CREATE NEW MOVEMENT  ###################################");

        // Not a valid direction for beginning movement.
        if (direction == Direction.STOP) return false;

        int currentX = playerClient.getCurrentMapLocation().getX();
        int currentY = playerClient.getCurrentMapLocation().getY();

        if (!isMovable(tmxMap, currentX + amountX, currentY + amountY)) {
            return false;
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
        new PlayerMove(direction).sendPacket();

        playerClient.getFutureMapLocation().add(amountX, amountY);
        walking = true;

         return true;
    }

    private boolean isMovable(TmxMap tmxMap, int x, int y) {

        if (!MapUtil.isTraversable(tmxMap, x, y)) {
            // Play sound or something
            return false;
        }

        if (MapUtil.isOutOfBounds(tmxMap, x, y)) {
            // Play sound or something
            return false;
        }

        return true;
    }

    private void finishPlayerMove() {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());

        //
        playerClient.setDrawX(playerClient.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(playerClient.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);

        playerClient.setPredictedDirection(Direction.STOP);

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

            if (walkTime <= MAX_TIME) return;

            if (playerClient.getPredictedDirection() == Direction.STOP) {
                finishPlayerMove();
            } else {

                // Change player directional information for continuing movement.
                playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
                playerClient.setMoveDirection(playerClient.getPredictedDirection());

                if (playerClient.getPredictedDirection() == Direction.UP)
                    playerClient.getFutureMapLocation().add(0, 1);
                if (playerClient.getPredictedDirection() == Direction.DOWN)
                    playerClient.getFutureMapLocation().add(0, -1);
                if (playerClient.getPredictedDirection() == Direction.LEFT)
                    playerClient.getFutureMapLocation().add(-1, 0);
                if (playerClient.getPredictedDirection() == Direction.RIGHT)
                    playerClient.getFutureMapLocation().add(1, 0);

                playerClient.setPredictedDirection(Direction.STOP);

                walkTime = 0;
            }
        }
    }

}
