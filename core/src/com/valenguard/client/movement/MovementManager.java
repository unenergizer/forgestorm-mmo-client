package com.valenguard.client.movement;

import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.Direction;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.maps.MapUtil;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.maps.data.TmxMap;
import com.valenguard.client.maps.data.Warp;
import com.valenguard.client.network.packet.out.PlayerMove;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.bind.SchemaOutputResolver;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MovementManager {

    private PathFinding pathFinding = new PathFinding();

    // todo : this number relates to how fast the client moves
    // todo: this should be read in from the server so we will change this at a later date
    private static final float MAX_TIME = .5f;

    @Getter // Live mouse movement updates
    private int mouseTileX, mouseTileY;
    @Getter
    private int clickTileX, clickTileY;
    @Getter
    private List<PathFinding.MoveNode> moveNodes;

    @AllArgsConstructor
    private class MovementInfo {
        private Entity entity;
        private float walkTime;
    }

    private Queue<MovementInfo> movingEntities = new ConcurrentLinkedQueue<MovementInfo>();

    public void addEntityToMove(Entity entity, Direction direction) {
        if (entity.isMoving()) {

            predictEntityMovement(entity, direction);
        } else {
            int amountX = 0;
            int amountY = 0;
            if (direction == Direction.DOWN) amountY = -1;
            if (direction == Direction.UP) amountY = 1;
            if (direction == Direction.LEFT) amountX = -1;
            if (direction == Direction.RIGHT) amountX = 1;
            startEntityMovement(entity, direction, amountX, amountY);
        }

    }

    private void startEntityMovement(Entity entity, Direction direction, int amountX, int amountY) {
        entity.setMoveDirection(direction);
        entity.setPredictedDirection(Direction.STOP);
        entity.getFutureMapLocation().add(amountX, amountY);
        movingEntities.add(new MovementInfo(entity, 0));
    }

    private void predictEntityMovement(Entity entity, Direction direction) {
        entity.setPredictedDirection(direction);
    }

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

        if (playerClient.isMoving()) {
            return predictFuturePlayerMovement(playerClient, tmxMap, amountX, amountY, direction);
        } else {
            return createNewPlayerMovement(tmxMap, playerClient, amountX, amountY, direction);
        }
    }

    private boolean predictFuturePlayerMovement(PlayerClient playerClient, TmxMap tmxMap, int amountX, int amountY, Direction predictedDirection) {

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

        predictEntityMovement(playerClient, predictedDirection);

        // Telling the server where the player plans on moving.
        new PlayerMove(predictedDirection).sendPacket();

        return true;
    }

    private boolean createNewPlayerMovement(TmxMap tmxMap, PlayerClient playerClient, int amountX, int amountY, Direction direction) {

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
        startEntityMovement(playerClient, direction, amountX, amountY);

        new PlayerMove(direction).sendPacket();

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

    public void tick(float delta) {
        for (MovementInfo movementInfo : movingEntities) {
            moveEntity(movementInfo, delta);
        }
    }

    private void moveEntity(MovementInfo movementInfo, float delta) {
        Entity entity = movementInfo.entity;

        if (!entity.isMoving()) return;

        // roughly 1.0f / 60.0f
        movementInfo.walkTime += delta;

        int currentX = entity.getCurrentMapLocation().getX();
        int currentY = entity.getCurrentMapLocation().getY();

        int futureX = entity.getFutureMapLocation().getX();
        int futureY = entity.getFutureMapLocation().getY();

        entity.setDrawX(Interpolation.linear.apply(currentX, futureX, movementInfo.walkTime / MAX_TIME) * ClientConstants.TILE_SIZE);
        entity.setDrawY(Interpolation.linear.apply(currentY, futureY, movementInfo.walkTime / MAX_TIME) * ClientConstants.TILE_SIZE);

        if (movementInfo.walkTime <= MAX_TIME) return;

        if (entity.getPredictedDirection() == Direction.STOP) {
            finishEntityMove(movementInfo);
        } else {
            continueEntityMove(movementInfo);
        }
    }

    private void finishEntityMove(MovementInfo movementInfo) {

        Entity entity = movementInfo.entity;

        entity.getCurrentMapLocation().set(entity.getFutureMapLocation());

        //
        entity.setDrawX(entity.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        entity.setDrawY(entity.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);

        // The player is no longer moving.
        entity.setMoveDirection(Direction.STOP);
        entity.setPredictedDirection(Direction.STOP);

        movingEntities.remove(movementInfo);
    }

    private void continueEntityMove(MovementInfo movementInfo) {

        Entity entity = movementInfo.entity;
        Direction predictedDirection = movementInfo.entity.getPredictedDirection();

        // Change player directional information for continuing movement.
        entity.getCurrentMapLocation().set(entity.getFutureMapLocation());
        entity.setMoveDirection(entity.getPredictedDirection());

        System.out.println("continueEntityMove()");
        if (entity.getPredictedDirection() == Direction.UP) entity.getFutureMapLocation().add(0, 1);
        if (entity.getPredictedDirection() == Direction.DOWN) entity.getFutureMapLocation().add(0, -1);
        if (entity.getPredictedDirection() == Direction.LEFT) entity.getFutureMapLocation().add(-1, 0);
        if (entity.getPredictedDirection() == Direction.RIGHT) entity.getFutureMapLocation().add(1, 0);

        // todo abstract this logic out
        int amountX = 0;
        int amountY = 0;
        if (predictedDirection == Direction.DOWN) amountY = -1;
        if (predictedDirection == Direction.UP) amountY = 1;
        if (predictedDirection == Direction.LEFT) amountX = -1;
        if (predictedDirection == Direction.RIGHT) amountX = 1;

        // todo check map warps thing?

        if (!isMovable(movementInfo.entity.getCurrentMapLocation().getMapData(),
                entity.getFutureMapLocation().getX() + amountX, entity.getFutureMapLocation().getY() + amountY)) {
            entity.setPredictedDirection(Direction.STOP);
        }

        movementInfo.walkTime = 0;
    }
}
