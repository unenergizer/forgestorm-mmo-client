package com.valenguard.client.game.movement;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.input.KeyBinds;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.util.MoveNode;

import java.util.Queue;

import lombok.Setter;

import static com.valenguard.client.util.Preconditions.checkNotNull;

public class KeyboardMovement {

    private final MoveDirection[] moveKeys = new MoveDirection[4];

    @Setter
    private boolean invalidated = true;

    public void keyDown(int keycode) {
        if (invalidated) return;

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        MoveDirection moveDirection = null;
        switch (keycode) {
            case KeyBinds.UP:
            case KeyBinds.UP_ALT:
                moveDirection = MoveDirection.NORTH;
                break;
            case KeyBinds.DOWN:
            case KeyBinds.DOWN_ALT:
                moveDirection = MoveDirection.SOUTH;
                break;
            case KeyBinds.LEFT:
            case KeyBinds.LEFT_ALT:
                moveDirection = MoveDirection.WEST;
                break;
            case KeyBinds.RIGHT:
            case KeyBinds.RIGHT_ALT:
                moveDirection = MoveDirection.EAST;
                break;
        }

        if (moveDirection == null) return;

        // New Entity click so lets cancel entityTracker
        Valenguard.getInstance().getEntityTracker().cancel();

        addMoveKey(moveDirection);

        ClientMovementProcessor clientMovementProcessor = Valenguard.getInstance().getClientMovementProcessor();

        Queue<MoveNode> futureMoveNode = clientMovementProcessor.getNodeForDirection(
                playerClient,
                MoveUtil.isEntityMoving(playerClient) ? playerClient.getFutureMapLocation() : playerClient.getCurrentMapLocation(),
                moveDirection);

        MoveNode node = futureMoveNode.peek();

        checkNotNull(node, "The node cannot be null!");

        clientMovementProcessor.preProcessMovement(
                new InputData(
                        ClientMovementProcessor.MovementInput.KEYBOARD,
                        futureMoveNode
                ));
    }

    public void keyUp(int keycode) {
        if (invalidated) return;

        // Player movement input release
        MoveDirection letOffDirection = null;
        switch (keycode) {
            case KeyBinds.UP:
            case KeyBinds.UP_ALT:
                removeMoveKey(letOffDirection = MoveDirection.NORTH);
                break;
            case KeyBinds.DOWN:
            case KeyBinds.DOWN_ALT:
                removeMoveKey(letOffDirection = MoveDirection.SOUTH);
                break;
            case KeyBinds.LEFT:
            case KeyBinds.LEFT_ALT:
                removeMoveKey(letOffDirection = MoveDirection.WEST);
                break;
            case KeyBinds.RIGHT:
            case KeyBinds.RIGHT_ALT:
                removeMoveKey(letOffDirection = MoveDirection.EAST);
                break;
        }

        if (letOffDirection == null) return;

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();

        MoveDirection remainingMoveDirection = getRemainingMoveKey();
        // The player is no longer pressing any keys
        if (remainingMoveDirection == null) {
            Valenguard.getInstance().getClientMovementProcessor().letOffAllKeys(playerClient);
            return;
        }

        if (letOffDirection == MoveDirection.NORTH && playerClient.getPredictedMoveDirection() == MoveDirection.NORTH ||
                letOffDirection == MoveDirection.SOUTH && playerClient.getPredictedMoveDirection() == MoveDirection.SOUTH ||
                letOffDirection == MoveDirection.WEST && playerClient.getPredictedMoveDirection() == MoveDirection.WEST ||
                letOffDirection == MoveDirection.EAST && playerClient.getPredictedMoveDirection() == MoveDirection.EAST) {
            playerClient.setPredictedMoveDirection(remainingMoveDirection);
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

    void invalidateKeys() {
        for (int i = 0; i < moveKeys.length; i++) {
            moveKeys[i] = null;
        }
        invalidated = true;
    }
}
