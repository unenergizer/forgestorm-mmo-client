package com.valenguard.client.game.movement;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.util.MoveNode;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ClientMovementProcessor {

    private InputData inputData;

    @Getter
    @Setter
    private MovementInput currentMovementInput = MovementInput.NONE;

    public void preProcessMovement(InputData latestInput) {
        this.inputData = latestInput;
    }

    public void processMovement(PlayerClient playerClient) {
        if (inputData == null) return;
        if (playerClient.isWarping()) return;

        if (MoveUtil.isEntityMoving(playerClient)) continueMove(playerClient);
        else startNewMove(playerClient);

        inputData = null;
    }

    void letOffAllKeys(PlayerClient playerClient) {
        playerClient.setPredictedMoveDirection(MoveDirection.NONE);
    }

    private void startNewMove(PlayerClient playerClient) {
        // Since the keyboard is the type of input and the player is stopped with will
        // predicted that the player is also moving to the location after the tile they
        // are moving toward to start us off
        if (inputData.getMovementInput() == MovementInput.KEYBOARD) {
            checkArgument(inputData.getMoveNodes().size() == 1, "The input nodes was not one for keyboard input.");

            playerClient.setPredictedMoveDirection(getPredictedDirection(playerClient, playerClient.getCurrentMapLocation()));
            Valenguard.getInstance().getClientPlayerMovementManager().playerMove(playerClient, inputData.getMoveNodes());

            currentMovementInput = MovementInput.KEYBOARD;

        } else if (inputData.getMovementInput() == MovementInput.MOUSE) {
            playerClient.setPredictedMoveDirection(MoveDirection.NONE);
            Valenguard.getInstance().getClientPlayerMovementManager().playerMove(playerClient, inputData.getMoveNodes());

            currentMovementInput = MovementInput.MOUSE;
        }
    }

    private void continueMove(PlayerClient playerClient) {
        if (inputData.getMovementInput() == MovementInput.KEYBOARD) {
            checkArgument(inputData.getMoveNodes().size() == 1, "The input nodes was not one for keyboard input.");
            checkArgument(currentMovementInput != MovementInput.NONE, "Tried to continue a move but the current movement type was NONE.");

            if (currentMovementInput == MovementInput.MOUSE) {
                MoveDirection predictedDirection = getPredictedDirection(playerClient, playerClient.getFutureMapLocation());

                checkArgument(predictedDirection != MoveDirection.NONE, "Move direction was NONE when it is not suppose to be.");

                playerClient.setPredictedMoveDirection(predictedDirection);
                Valenguard.getInstance().getClientPlayerMovementManager().playerMove(playerClient, inputData.getMoveNodes());

            } else if (currentMovementInput == MovementInput.KEYBOARD) {
                MoveDirection predictedDirection = getPredictedDirection(playerClient, playerClient.getFutureMapLocation());
                playerClient.setPredictedMoveDirection(predictedDirection);
            }

            currentMovementInput = MovementInput.KEYBOARD;

        } else if (inputData.getMovementInput() == MovementInput.MOUSE) {
            // This just overwrite the current queue
            playerClient.setPredictedMoveDirection(MoveDirection.NONE);
            Valenguard.getInstance().getClientPlayerMovementManager().playerMove(playerClient, inputData.getMoveNodes());

            currentMovementInput = MovementInput.MOUSE;
        }
    }

    private MoveDirection getPredictedDirection(PlayerClient playerClient, Location currentLocation) {
        MoveNode moveNode = inputData.getMoveNodes().peek();
        checkNotNull(moveNode, "The move node was null!");
        Location predictedFutureMapLocation = new Location(playerClient.getMapName(), moveNode.getWorldX(), moveNode.getWorldY());
        return MoveUtil.getMoveDirection(currentLocation, predictedFutureMapLocation);
    }

    Queue<MoveNode> getNodeForDirection(PlayerClient playerClient, Location currentPlayerLocation, MoveDirection direction) {
        Queue<MoveNode> nextNode = new LinkedList<MoveNode>();
        Location futureLocation = MoveUtil.getLocation(playerClient.getGameMap(), direction).add(currentPlayerLocation);
        nextNode.add(new MoveNode(futureLocation.getX(), futureLocation.getY(), 0, 0));
        return nextNode;
    }

    void invalidateAllInput() {
        inputData = null;
        currentMovementInput = MovementInput.NONE;
        EntityManager.getInstance().getPlayerClient().setPredictedMoveDirection(MoveDirection.NONE);
        Valenguard.gameScreen.getKeyboard().getKeyboardMovement().invalidateKeys();
        Valenguard.getInstance().getMouseManager().invalidateMouse();
    }

    public enum MovementInput {
        KEYBOARD,
        MOUSE,
        NONE
    }
}
