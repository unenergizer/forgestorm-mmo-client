package com.valenguard.client.game.movement;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.util.MoveNode;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;
import static com.valenguard.client.util.Preconditions.checkArgument;
import static com.valenguard.client.util.Preconditions.checkNotNull;

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

        println(getClass(), "=======================================", true, ClientConstants.MONITOR_MOVEMENT_CHECKS);
        println(getClass(), "inputInfo: type = " + inputData.getMovementInput() + " , nodesSize = " + inputData.getMoveNodes(), true, ClientConstants.MONITOR_MOVEMENT_CHECKS);
        println(getClass(), "currentMovementInput = " + currentMovementInput, true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

        if (playerClient.isWarping()) return;

        if (MoveUtil.isEntityMoving(playerClient)) continueMove(playerClient);
        else startNewMove(playerClient);

        inputData = null;
    }

    void letOffAllKeys(PlayerClient playerClient) {
        playerClient.setPredictedMoveDirection(MoveDirection.NONE);
    }

    private void startNewMove(PlayerClient playerClient) {

        println(getClass(), "Starting a new move", true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

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
            Valenguard.getInstance().getClientPlayerMovementManager().playerMove(playerClient, inputData.getMoveNodes(), inputData.getAbstractPostProcessor());

            currentMovementInput = MovementInput.MOUSE;
        }
    }

    private void continueMove(PlayerClient playerClient) {

        println(getClass(), "Continuing a move", true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

        if (inputData.getMovementInput() == MovementInput.KEYBOARD) {
            checkArgument(inputData.getMoveNodes().size() == 1, "The input nodes was not one for keyboard input.");
            checkArgument(currentMovementInput != MovementInput.NONE, "Tried to continue a move but the current movement type was NONE.");

            if (currentMovementInput == MovementInput.MOUSE) {
                MoveDirection predictedDirection = getPredictedDirection(playerClient, playerClient.getFutureMapLocation());

                checkArgument(predictedDirection != MoveDirection.NONE, "Move direction was NONE when it is not suppose to be.");

                playerClient.setPredictedMoveDirection(predictedDirection);
                Valenguard.getInstance().getClientPlayerMovementManager().playerMove(playerClient, inputData.getMoveNodes(), inputData.getAbstractPostProcessor());

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
        nextNode.add(new MoveNode(futureLocation.getX(), futureLocation.getY(), (short) 0, (short) 0));
        return nextNode;
    }

    public void invalidateAllInput() {
        inputData = null;
        currentMovementInput = MovementInput.NONE;
        Valenguard.gameScreen.getKeyboard().getKeyboardMovement().invalidateKeys();
        Valenguard.getInstance().getMouseManager().invalidateMouse();
        if (EntityManager.getInstance().getPlayerClient() != null) {
            EntityManager.getInstance().getPlayerClient().setPredictedMoveDirection(MoveDirection.NONE);
        }
    }

    public void resetInput() {
        inputData = null;
        currentMovementInput = MovementInput.NONE;
        if (EntityManager.getInstance().getPlayerClient() != null) {
            EntityManager.getInstance().getPlayerClient().setPredictedMoveDirection(MoveDirection.NONE);
        }
    }

    public enum MovementInput {
        KEYBOARD,
        MOUSE,
        NONE
    }
}
