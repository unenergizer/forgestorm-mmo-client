package com.valenguard.client.game.movement;

import com.badlogic.gdx.math.Interpolation;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.maps.GameMap;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.game.world.maps.MapUtil;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.network.game.packet.out.PlayerMovePacketOut;
import com.valenguard.client.util.FadeOut;
import com.valenguard.client.util.MoveNode;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;
import static com.valenguard.client.util.Preconditions.checkArgument;

public class ClientPlayerMovementManager {

    private static final boolean PRINT_DEBUG = false;

    @Getter
    private Queue<MoveNode> movements = new LinkedList<MoveNode>();
    private AbstractPostProcessor abstractPostProcessor;

    void playerMove(PlayerClient playerClient, Queue<MoveNode> movements) {
        playerClient.closeBankWindow();
        ActorUtil.getStageHandler().getEntityShopWindow().closeShopWindow(true);
        playerMove(playerClient, movements, null);
    }

    void playerMove(PlayerClient playerClient, Queue<MoveNode> movements, AbstractPostProcessor abstractPostProcessor) {
        if (movements.isEmpty()) return;
        this.movements = movements;
        if (abstractPostProcessor != null) this.abstractPostProcessor = abstractPostProcessor;
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
        playerClient.closeBankWindow();
        ActorUtil.getStageHandler().getEntityShopWindow().closeShopWindow(true);

        println(getClass(), "Processing next node", true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

        MoveNode nextNode = movements.remove();

        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        Location currentLocation = playerClient.getCurrentMapLocation();
        Location futureLocation = new Location(playerClient.getMapName(), nextNode.getWorldX(), nextNode.getWorldY());
        playerClient.setFutureMapLocation(futureLocation);
        MoveDirection moveDirection = MoveUtil.getMoveDirection(currentLocation, futureLocation);

        println(getClass(), "Current Location: " + currentLocation, false, PRINT_DEBUG);
        println(getClass(), "Future Location: " + futureLocation, false, PRINT_DEBUG);

        int differenceX = Math.abs(playerClient.getCurrentMapLocation().getX() - playerClient.getFutureMapLocation().getX());
        int differenceY = Math.abs(playerClient.getCurrentMapLocation().getY() - playerClient.getFutureMapLocation().getY());

        checkArgument(differenceX + differenceY == 1, "The total difference in movement was not equal to one : " + differenceX + differenceY);
        checkArgument(moveDirection != MoveDirection.NONE, "The move direction cannot be NONE");

        playerClient.setFacingDirection(moveDirection);
        playerClient.setWalkTime(0f);

        if (MapUtil.isWarp(playerClient.getGameMap(), futureLocation.getX(), futureLocation.getY())) {
            println(getClass(), "We hit a tile that is a warp.", false, PRINT_DEBUG);

            movements.clear();
            playerClient.setWarping(true);
            Valenguard.getInstance().getClientMovementProcessor().invalidateAllInput();

            // Since we are warping, fade out the screen!
            ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);

            // Close windows
            ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getItemDropDownMenu());
            ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getEntityDropDownMenu());
        }

        new PlayerMovePacketOut(futureLocation).sendPacket();
    }

    public void processMoveNodes(PlayerClient playerClient, float delta) {

        if (!MoveUtil.isEntityMoving(playerClient)) return;

        // Fades the mouse out as soon as the player starts moving.
        FadeOut mouseFadeOut = Valenguard.getInstance().getMouseManager().getFadeOut();
        if (!mouseFadeOut.isFading()) {
            mouseFadeOut.startFade(MouseManager.NUM_TICKS_TO_FADE_MOUSE);
        }

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

                println(getClass(), "Predicted to move  the player: " + predictedDirection, false, PRINT_DEBUG);

                // Setting the future here to prevent the snapping forward of
                // the player on the next startTracking.
                playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
                Queue<MoveNode> singleMoveNode = Valenguard.getInstance().getClientMovementProcessor().getNodeForDirection(
                        playerClient,
                        playerClient.getFutureMapLocation(),
                        playerClient.getPredictedMoveDirection());

                MoveNode possibleMove = singleMoveNode.peek();
                if (possibleMove != null && !isMovable(playerClient.getGameMap(), possibleMove.getWorldX(), possibleMove.getWorldY())) {
                    finishMove(playerClient);
                } else {
                    playerMove(playerClient, singleMoveNode);
                }

            } else {
                finishMove(playerClient);
            }
        } else {
            processNextNode(playerClient);
        }

    }

    private boolean isMovable(GameMap gameMap, short x, short y) {
        if (!MapUtil.isTraversable(gameMap, x, y)) return false;
        return !MapUtil.isOutOfBounds(gameMap, x, y);
    }

    private void finishMove(PlayerClient playerClient) {
        println(getClass(), "Finished Movement", true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        playerClient.setDrawX(playerClient.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(playerClient.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);

        // The player is no longer moving.
        playerClient.setPredictedMoveDirection(MoveDirection.NONE);
        Valenguard.getInstance().getClientMovementProcessor().setCurrentMovementInput(ClientMovementProcessor.MovementInput.NONE);

        if (abstractPostProcessor != null) {
            abstractPostProcessor.postMoveAction();
            abstractPostProcessor = null;
        }
    }
}
