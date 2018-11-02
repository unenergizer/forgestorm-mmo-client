package com.valenguard.client.movement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.MoveDirection;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.input.KeyBinds;
import com.valenguard.client.maps.MapUtil;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.maps.data.TmxMap;
import com.valenguard.client.maps.data.Warp;
import com.valenguard.client.network.packet.out.PlayerMove;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.Queue;

import lombok.Setter;

public class ClientPlayerMovementManager {

    private final MoveDirection[] moveKeys = new MoveDirection[4];

    @Setter
    private boolean processingNodes = false;

    public void keyDown(int keycode) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        MoveDirection moveDirection = null;
        Location moveLocation = null;
        switch (keycode) {
            case KeyBinds.UP:
            case KeyBinds.UP_ALT:
                moveLocation = MoveUtil.getLocation(playerClient.getTmxMap(), moveDirection = MoveDirection.UP);
                break;
            case KeyBinds.DOWN:
            case KeyBinds.DOWN_ALT:
                moveLocation = MoveUtil.getLocation(playerClient.getTmxMap(), moveDirection = MoveDirection.DOWN);
                break;
            case KeyBinds.LEFT:
            case KeyBinds.LEFT_ALT:
                moveLocation = MoveUtil.getLocation(playerClient.getTmxMap(), moveDirection = MoveDirection.LEFT);
                break;
            case KeyBinds.RIGHT:
            case KeyBinds.RIGHT_ALT:
                moveLocation = MoveUtil.getLocation(playerClient.getTmxMap(), moveDirection = MoveDirection.RIGHT);
                break;
        }

        if (moveDirection != null && moveLocation != null) {
            addMoveKey(moveDirection);
            processingNodes = false;
            Valenguard.getInstance().getMouseManager().setMoveNodes(null);
            playerMove(playerClient, moveLocation.getX(), moveLocation.getY(), moveDirection);
        }
    }

    public void keyUp(int keycode) {
        // Player movement input release
        switch (keycode) {
            case KeyBinds.UP:
            case KeyBinds.UP_ALT:
                removeMoveKey(MoveDirection.UP);
                break;
            case KeyBinds.DOWN:
            case KeyBinds.DOWN_ALT:
                removeMoveKey(MoveDirection.DOWN);
                break;
            case KeyBinds.LEFT:
            case KeyBinds.LEFT_ALT:
                removeMoveKey(MoveDirection.LEFT);
                break;
            case KeyBinds.RIGHT:
            case KeyBinds.RIGHT_ALT:
                removeMoveKey(MoveDirection.RIGHT);
                break;
        }

        MoveDirection moveDirection = getRemainingMoveKey();
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        if (moveDirection != null) {
            Location location = MoveUtil.getLocation(playerClient.getTmxMap(), moveDirection);
            if (location != null)
                playerMove(playerClient, location.getX(), location.getY(), moveDirection);
        } else {
            playerMove(playerClient, 0, 0, MoveDirection.NONE);
        }
    }

    private void removeMoveKey(MoveDirection moveDirection) {
        boolean found = false;
        for (int i = 0; i < moveKeys.length; i++) {
            if (moveKeys[i] == moveDirection) {
                moveKeys[i] = null;
                found = true;
            }
            if (found) {
                if (i + 1 >= moveKeys.length) break;
                moveKeys[i] = moveKeys[i + 1];
            }
        }
    }

    private void addMoveKey(MoveDirection moveDirection) {
        for (int i = 0; i < moveKeys.length; i++) {
            if (moveKeys[i] == null) {
                moveKeys[i] = moveDirection;
                break;
            }
        }
    }

    private MoveDirection getRemainingMoveKey() {
        for (MoveDirection moveKey : moveKeys) {
            if (moveKey != null) return moveKey;
        }
        return null;
    }

    private void playerMove(PlayerClient playerClient, int amountX, int amountY, MoveDirection moveDirection) {
        if (MoveUtil.isEntityMoving(playerClient)) {
            predictFuturePlayerMovement(playerClient, playerClient.getTmxMap(), amountX, amountY, moveDirection);
        } else {
            createNewPlayerMovement(playerClient.getTmxMap(), playerClient, amountX, amountY, moveDirection);
        }
    }

    private void predictFuturePlayerMovement(PlayerClient playerClient, TmxMap tmxMap, int amountX, int amountY, MoveDirection predictedMoveDirection) {

        Location futureMapLocation = playerClient.getFutureMapLocation();

        // Already predicting that the player will move in that direction.
        if (playerClient.getPredictedMoveDirection() == predictedMoveDirection) return;

        // The predicted movement is not possible based on input.
        if (!isMovable(tmxMap, futureMapLocation.getX() + amountX, futureMapLocation.getY() + amountY))
            return;

        // Todo: check map warps

        playerClient.setPredictedMoveDirection(predictedMoveDirection);
    }

    private void createNewPlayerMovement(TmxMap tmxMap, PlayerClient playerClient, int amountX, int amountY, MoveDirection moveDirection) {

        // Not a valid moveDirection for beginning movement.
        if (moveDirection == MoveDirection.NONE) return;

        int currentX = playerClient.getCurrentMapLocation().getX();
        int currentY = playerClient.getCurrentMapLocation().getY();

        if (!isMovable(tmxMap, currentX + amountX, currentY + amountY)) return;

        if (MapUtil.hasWarp(tmxMap, currentX + amountX, currentY + amountY)) {
            // Play sound or something
            Warp warp = MapUtil.getWarp(tmxMap, currentX + amountX, currentY + amountY);
//            System.out.println("[WARP] Map: " + warp.getDestinationMapName()
//                    + ", FaceDirection: " + warp.getMoveDirectionToFace().toString()
//                    + ", X: " + warp.getX()
//                    + ", Y: " + warp.getY());
        }

        playerClient.setPredictedMoveDirection(moveDirection);
        sendNewMovementInfo(playerClient, moveDirection, amountX, amountY);
    }

    private void sendNewMovementInfo(PlayerClient playerClient, MoveDirection moveDirection, int amountX, int amountY) {
        // Setting the player's moveDirection for movement.
        playerClient.getFutureMapLocation().add(amountX, amountY);
        playerClient.setWalkTime(0f);

        new PlayerMove(moveDirection).sendPacket();
    }

    private boolean isMovable(TmxMap tmxMap, int x, int y) {

        if (!MapUtil.isTraversable(tmxMap, x, y)) {
            // Play sound or something
            return false;
        }

        return !MapUtil.isOutOfBounds(tmxMap, x, y);
    }

    private boolean isMovable(Location location) {
        return isMovable(location.getMapData(), location.getX(), location.getY());
    }

    public void tick(float delta) {

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        int currentX = playerClient.getCurrentMapLocation().getX();
        int currentY = playerClient.getCurrentMapLocation().getY();

        int futureX = playerClient.getFutureMapLocation().getX();
        int futureY = playerClient.getFutureMapLocation().getY();

        boolean isMoving = MoveUtil.isEntityMoving(playerClient);
        Queue<PathFinding.MoveNode> moveNodes = Valenguard.getInstance().getMouseManager().getMoveNodes();

        if (!isMoving && moveNodes == null) return;

        if (!isMoving && !processingNodes) {
            startProcessingNodes(moveNodes, playerClient, currentX, currentY);
            return;
        }

        playerClient.setWalkTime(playerClient.getWalkTime() + delta);

        playerClient.setDrawX(Interpolation.linear.apply(currentX, futureX, playerClient.getWalkTime() / playerClient.getMoveSpeed()) * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(Interpolation.linear.apply(currentY, futureY, playerClient.getWalkTime() / playerClient.getMoveSpeed()) * ClientConstants.TILE_SIZE);

        // TODO: Better names
        if (playerClient.getWalkTime() <= playerClient.getMoveSpeed()) return;

        if (processingNodes) {
            processNextNode(moveNodes, playerClient);
            return;
        }

        // If they are not predicting to move then stop them.
        if (playerClient.getPredictedMoveDirection() == MoveDirection.NONE) {
            finishPlayerMove(playerClient);
        } else {
            continuePlayerMove(playerClient);
        }
    }

    private void startProcessingNodes(Queue<PathFinding.MoveNode> moveNodes, PlayerClient playerClient, int currentX, int currentY) {
        PathFinding.MoveNode nextNode = moveNodes.remove();
//        System.out.println();
//        System.out.println("Start => [" + currentX + ", " + currentY + "] <-> [" + nextNode.getWorldX() + ", " + nextNode.getWorldY() + "]");
        MoveDirection moveDirection = MoveUtil.getMoveDirection(currentX, currentY, nextNode.getWorldX(), nextNode.getWorldY());
        Location moveLocation = MoveUtil.getLocation(playerClient.getTmxMap(), moveDirection);

        processingNodes = true;
        sendNewMovementInfo(playerClient, moveDirection, moveLocation.getX(), moveLocation.getY());
    }

    private void processNextNode(Queue<PathFinding.MoveNode> moveNodes, PlayerClient playerClient) {
        // We have arrived.
        if (moveNodes.isEmpty()) {
            Valenguard.getInstance().getMouseManager().setMoveNodes(null);
            finishPlayerMove(playerClient);
            processingNodes = false;
            return;
        }

        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        int currentX = playerClient.getCurrentMapLocation().getX();
        int currentY = playerClient.getCurrentMapLocation().getY();

        PathFinding.MoveNode nextNode = moveNodes.remove();
        MoveDirection moveDirection = MoveUtil.getMoveDirection(currentX, currentY, nextNode.getWorldX(), nextNode.getWorldY());

//        System.out.println("Next => [" + currentX + ", " + currentY + "] <-> [" + nextNode.getWorldX() + ", " + nextNode.getWorldY() + "]");

        playerClient.setFutureMapLocation(new Location(playerClient.getMapName(), nextNode.getWorldX(), nextNode.getWorldY()));
        new PlayerMove(moveDirection).sendPacket();
        playerClient.setWalkTime(0f);
    }

    private void finishPlayerMove(PlayerClient playerClient) {
//        System.out.println("Finished a move");
        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        playerClient.setDrawX(playerClient.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(playerClient.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);

        // The player is no longer moving.
        playerClient.setPredictedMoveDirection(MoveDirection.NONE);
    }

    private void continuePlayerMove(PlayerClient playerClient) {

        // Change player directional information for continuing movement.
        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());

        Location addToLocation = MoveUtil.getLocation(playerClient.getTmxMap(), playerClient.getPredictedMoveDirection());
        Location attemptLocation = new Location(playerClient.getFutureMapLocation()).add(addToLocation);

        // todo check map warps thing?

        if (!isMovable(attemptLocation)) {
            finishPlayerMove(playerClient);
        } else {

            MoveDirection moveDirection = MoveUtil.getMoveDirection(playerClient.getCurrentMapLocation(), attemptLocation);
            if ((moveDirection == MoveDirection.UP && !Gdx.input.isKeyPressed(Input.Keys.W)) ||
                    (moveDirection == MoveDirection.DOWN && !Gdx.input.isKeyPressed(Input.Keys.S)) ||
                    (moveDirection == MoveDirection.LEFT && !Gdx.input.isKeyPressed(Input.Keys.A)) ||
                    (moveDirection == MoveDirection.RIGHT && !Gdx.input.isKeyPressed(Input.Keys.D))) {

                MoveDirection newMoveDirection = getRemainingMoveKey();

                if (newMoveDirection == null) {
                    finishPlayerMove(playerClient);
                    return;
                }

                Location newAddToLocation = MoveUtil.getLocation(playerClient.getTmxMap(), newMoveDirection);
                Location newAttemptLocation = new Location(playerClient.getFutureMapLocation()).add(newAddToLocation);

                if (!isMovable(newAttemptLocation)) {
                    finishPlayerMove(playerClient);
                } else {
                    playerClient.setFutureMapLocation(newAttemptLocation);
                    playerClient.setPredictedMoveDirection(newMoveDirection);
                    new PlayerMove(playerClient.getPredictedMoveDirection()).sendPacket();
                }
            } else {
                playerClient.setFutureMapLocation(attemptLocation);
                new PlayerMove(playerClient.getPredictedMoveDirection()).sendPacket();
            }
        }

        playerClient.setWalkTime(0f);
    }
}
