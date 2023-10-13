package com.forgestorm.client.game.movement;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.KeyBinds;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.util.MoveNode;
import com.forgestorm.shared.game.world.maps.MoveDirection;
import lombok.Setter;

import java.util.Arrays;
import java.util.Queue;

import static com.forgestorm.client.util.Preconditions.checkNotNull;

public class KeyboardMovement {

    private final ClientMain clientMain;
    private final MoveDirection[] moveKeys = new MoveDirection[4];

    @Setter
    private boolean invalidated = true;

    public KeyboardMovement(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    public void keyDown(int keycode) {
        if (invalidated) return;

        PlayerClient playerClient = clientMain.getEntityManager().getPlayerClient();

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

        // New Entity click so lets cancelFollow entityTracker
        clientMain.getEntityTracker().cancelFollow();

        addMoveKey(moveDirection);

        ClientMovementProcessor clientMovementProcessor = clientMain.getClientMovementProcessor();

        Queue<MoveNode> futureMoveNode = clientMovementProcessor.getNodeForDirection(
                playerClient,
                MoveUtil.isEntityMoving(playerClient) ? playerClient.getFutureMapLocation() : playerClient.getCurrentMapLocation(),
                moveDirection);

        MoveNode node = futureMoveNode.peek();

        checkNotNull(node, "The node cannot be null!");

        clientMovementProcessor.postProcessMovement(
                new InputData(
                        ClientMovementProcessor.MovementInput.KEYBOARD,
                        futureMoveNode,
                        null
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

        PlayerClient playerClient = clientMain.getEntityManager().getPlayerClient();

        MoveDirection remainingMoveDirection = getRemainingMoveKey();
        // The player is no longer pressing any keys
        if (remainingMoveDirection == null) {
            clientMain.getClientMovementProcessor().letOffAllKeys(playerClient);
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
        Arrays.fill(moveKeys, null);
        invalidated = true;
    }
}
