package com.valenguard.client.game.movement;

import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.maps.MapUtil;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.GameMap;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.network.packet.out.PlayerMove;
import com.valenguard.client.util.Log;
import com.valenguard.client.util.MoveNode;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkArgument;

public class ClientPlayerMovementManager {

    private static final boolean PRINT_DEBUG = false;

    @Getter
    private Queue<MoveNode> movements = new LinkedList<MoveNode>();

    void playerMove(PlayerClient playerClient, Queue<MoveNode> nodes) {
        checkArgument(!nodes.isEmpty(), "Tried to tell the player to move nowhere.");
        movements = nodes;
        // todo: consider checking to see if they're moving

        if (!checkSingleNode(playerClient)) return;
        if (!MoveUtil.isEntityMoving(playerClient)) processNextNode(playerClient);
    }

    private boolean checkSingleNode(PlayerClient playerClient) {
        if (movements.size() == 1) {
            MoveNode node = movements.peek();
            if (!isMovable(playerClient.getGameMap(), node.getWorldX(), node.getWorldY())) {
                movements.clear();
                playerClient.setPredictedMoveDirection(MoveDirection.NONE);
                return false;
            }
        }
        return true;
    }

    private void processNextNode(PlayerClient playerClient) {

        Log.println(getClass(), "Processing next node", true, ClientConstants.MINITOR_MOVEMENT_BUG);

        MoveNode nextNode = movements.remove();

        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        Location currentLocation = playerClient.getCurrentMapLocation();
        Location futureLocation = new Location(playerClient.getMapName(), nextNode.getWorldX(), nextNode.getWorldY());
        playerClient.setFutureMapLocation(futureLocation);
        MoveDirection moveDirection = MoveUtil.getMoveDirection(currentLocation, futureLocation);

        Log.println(getClass(), "Current Location: " + currentLocation, false, PRINT_DEBUG);
        Log.println(getClass(), "Future Location: " + futureLocation, false, PRINT_DEBUG);

        int differenceX = Math.abs(playerClient.getCurrentMapLocation().getX() - playerClient.getFutureMapLocation().getX());
        int differenceY = Math.abs(playerClient.getCurrentMapLocation().getY() - playerClient.getFutureMapLocation().getY());

        checkArgument(differenceX + differenceY == 1, "The total difference in movement was not equal to one : " + differenceX + differenceY);
        checkArgument(moveDirection != MoveDirection.NONE, "The move direction cannot be NONE");

        playerClient.setFacingDirection(moveDirection);
        playerClient.setWalkTime(0f);

        if (MapUtil.isWarp(playerClient.getGameMap(), futureLocation.getX(), futureLocation.getY())) {
            Log.println(getClass(), "We hit a tile that is a warp.", false, PRINT_DEBUG);

            movements.clear();
            playerClient.setWarping(true);
            Valenguard.getInstance().getClientMovementProcessor().invalidateAllInput();
        }

        new PlayerMove(moveDirection).sendPacket();
    }

    public void processMoveNodes(PlayerClient playerClient, float delta) {

        if (!MoveUtil.isEntityMoving(playerClient)) return;

        int currentX = playerClient.getCurrentMapLocation().getX();
        int currentY = playerClient.getCurrentMapLocation().getY();

        int futureX = playerClient.getFutureMapLocation().getX();
        int futureY = playerClient.getFutureMapLocation().getY();

        playerClient.setWalkTime(playerClient.getWalkTime() + delta);

        playerClient.setDrawX(Interpolation.linear.apply(currentX, futureX, playerClient.getWalkTime() / playerClient.getMoveSpeed()) * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(Interpolation.linear.apply(currentY, futureY, playerClient.getWalkTime() / playerClient.getMoveSpeed()) * ClientConstants.TILE_SIZE);

        if (playerClient.getWalkTime() <= playerClient.getMoveSpeed()) return;

        // There are no more movements to go
        if (movements.isEmpty()) {
            if (playerClient.getPredictedMoveDirection() != MoveDirection.NONE) {

                MoveDirection predictedDirection = playerClient.getPredictedMoveDirection();

                Log.println(getClass(), "Predicted to move  the player: " + predictedDirection, false, PRINT_DEBUG);

                // Setting the future here to prevent the snapping forward of
                // the player on the next tick.
                playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
                Queue<MoveNode> singleMoveNode = Valenguard.getInstance().getClientMovementProcessor().getNodeForDirection(
                        playerClient,
                        playerClient.getFutureMapLocation(),
                        playerClient.getPredictedMoveDirection());
                playerMove(playerClient, singleMoveNode);

            } else {
                finishMove(playerClient);
            }
        } else {
            processNextNode(playerClient);
        }

    }

    private boolean isMovable(GameMap gameMap, int x, int y) {
        if (!MapUtil.isTraversable(gameMap, x, y)) return false;
        return !MapUtil.isOutOfBounds(gameMap, x, y);
    }

    private void finishMove(PlayerClient playerClient) {

        Log.println(getClass(), "Finished Movement", true, ClientConstants.MINITOR_MOVEMENT_BUG);

        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        playerClient.setDrawX(playerClient.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(playerClient.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);

        // The player is no longer moving.
        playerClient.setPredictedMoveDirection(MoveDirection.NONE);
        Valenguard.getInstance().getClientMovementProcessor().setCurrentMovementInput(ClientMovementProcessor.MovementInput.NONE);
    }

}
