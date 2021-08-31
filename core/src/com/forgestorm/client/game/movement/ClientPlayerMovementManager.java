package com.forgestorm.client.game.movement;

import com.badlogic.gdx.math.Interpolation;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.game.world.maps.Warp;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.game.world.maps.WorldUtil;
import com.forgestorm.client.network.game.packet.out.PlayerMovePacketOut;
import com.forgestorm.client.util.FadeOut;
import com.forgestorm.client.util.MoveNode;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;
import static com.forgestorm.client.util.Preconditions.checkArgument;

public class ClientPlayerMovementManager {

    private static final boolean PRINT_DEBUG = false;

    @Getter
    private Queue<MoveNode> movements = new LinkedList<MoveNode>();
    private AbstractPostProcessor abstractPostProcessor;

    @Getter
    private final Queue<MoveNode> movesSentToServer = new LinkedList<MoveNode>();

    void playerMove(PlayerClient playerClient, Queue<MoveNode> movements) {
        playerClient.closeBankWindow();
        ActorUtil.getStageHandler().getPagedItemStackWindow().closePagedWindow(true);
        playerMove(playerClient, movements, null);
    }

    void playerMove(PlayerClient playerClient, Queue<MoveNode> movements, AbstractPostProcessor abstractPostProcessor) {
        if (movements.isEmpty()) return;
        this.movements = movements;
//        if (abstractPostProcessor != null) this.abstractPostProcessor = abstractPostProcessor;
        this.abstractPostProcessor = abstractPostProcessor;
        // todo: consider checking to see if they're moving

        if (!checkSingleNode(playerClient)) return;
        if (!MoveUtil.isEntityMoving(playerClient)) processNextNode(playerClient);
    }

    private boolean checkSingleNode(PlayerClient playerClient) {
        if (movements.size() == 1) {
            MoveNode node = movements.peek();
            if (!WorldUtil.isTraversable(node.getWorldX(), node.getWorldY())) {
                movements.clear();
                playerClient.setPredictedMoveDirection(MoveDirection.NONE);
                return false;
            }
        }
        return true;
    }

    private void processNextNode(PlayerClient playerClient) {
        playerClient.closeBankWindow();
        ActorUtil.getStageHandler().getPagedItemStackWindow().closePagedWindow(true);

        println(getClass(), "Processing next node", true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

        MoveNode nextNode = movements.remove();

        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        GameWorld gameWorld = playerClient.getGameMap();
        Location currentLocation = playerClient.getCurrentMapLocation();
        Location futureLocation = new Location(playerClient.getWorldName(), nextNode.getWorldX(), nextNode.getWorldY(), currentLocation.getZ());
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

        // Check chunk change
        WorldChunk currentChunk = currentLocation.getLocationChunk();
        WorldChunk futureChunk = futureLocation.getLocationChunk();
        if (!gameWorld.isSameChunk(currentChunk, futureChunk)) {
            gameWorld.playerChunkChange(playerClient);
        }

        // Warp checks
        if (WorldUtil.isWarp(futureLocation.getX(), futureLocation.getY())) {
            println(getClass(), "We hit a tile that is a warp.", false, true);

            Warp warp = WorldUtil.getWarp(futureLocation.getX(), futureLocation.getY());
            println(getClass(), warp.getWarpDestination().toString());
            println(getClass(), warp.getDirectionToFace().getDirectionName());

            movements.clear();
//            playerClient.setWarping(true);
            ClientMain.getInstance().getClientMovementProcessor().invalidateAllInput();

            // Since we are warping, fade out the screen!
            if (playerClient.getCurrentMapLocation().getWorldName().equals(warp.getWarpDestination().getWorldName())) {
                println(getClass(), "WARP LOCATION WORLD NAME AND CURRENT WORLD NAME MATCH", true);
            } else {
                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);
            }

            // Close windows
            ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getItemDropDownMenu());
            ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getEntityDropDownMenu());
        }

        movesSentToServer.add(nextNode);

        new PlayerMovePacketOut(futureLocation).sendPacket();
    }

    public void processMoveNodes(PlayerClient playerClient) {

        if (!MoveUtil.isEntityMoving(playerClient)) return;

        // Fades the mouse out as soon as the player starts moving.
        FadeOut mouseFadeOut = ClientMain.getInstance().getMouseManager().getFadeOut();
        if (!mouseFadeOut.isFading()) {
            mouseFadeOut.startFade(MouseManager.NUM_TICKS_TO_FADE_MOUSE);
        }

        int currentX = playerClient.getCurrentMapLocation().getX();
        int currentY = playerClient.getCurrentMapLocation().getY();

        int futureX = playerClient.getFutureMapLocation().getX();
        int futureY = playerClient.getFutureMapLocation().getY();

        int slowDown = 1;
        if (movesSentToServer.size() > 1) {
            slowDown = movesSentToServer.size();
        }

        // TODO: Include delta variable in calculation

        println(getClass(), "Size of slow down queue: " + slowDown, true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

        float frameMove = (playerClient.getMoveSpeed() / 60F) / slowDown;

        playerClient.setWalkTime(playerClient.getWalkTime() + frameMove);

        float interpolatedX = Interpolation.linear.apply(currentX, futureX, playerClient.getWalkTime()) * ClientConstants.TILE_SIZE;
        float interpolatedY = Interpolation.linear.apply(currentY, futureY, playerClient.getWalkTime()) * ClientConstants.TILE_SIZE;

        playerClient.setDrawX(interpolatedX);
        playerClient.setDrawY(interpolatedY);

        if (playerClient.getWalkTime() < 1.0F) return;

        // There are no more movements to go
        if (movements.isEmpty()) {

            if (playerClient.getPredictedMoveDirection() != MoveDirection.NONE) {

                MoveDirection predictedDirection = playerClient.getPredictedMoveDirection();

                println(getClass(), "Predicted to move  the player: " + predictedDirection, false, PRINT_DEBUG);

                // Setting the future here to prevent the snapping forward of
                // the player on the next follow.
                playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
                Queue<MoveNode> singleMoveNode = ClientMain.getInstance().getClientMovementProcessor().getNodeForDirection(
                        playerClient,
                        playerClient.getFutureMapLocation(),
                        playerClient.getPredictedMoveDirection());

                MoveNode possibleMove = singleMoveNode.peek();
                if (possibleMove != null && !WorldUtil.isTraversable(possibleMove.getWorldX(), possibleMove.getWorldY())) {
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

    private void finishMove(PlayerClient playerClient) {
        println(getClass(), "Finished Movement", true, ClientConstants.MONITOR_MOVEMENT_CHECKS);

        playerClient.getCurrentMapLocation().set(playerClient.getFutureMapLocation());
        playerClient.setDrawX(playerClient.getFutureMapLocation().getX() * ClientConstants.TILE_SIZE);
        playerClient.setDrawY(playerClient.getFutureMapLocation().getY() * ClientConstants.TILE_SIZE);

        // The player is no longer moving.
        playerClient.setPredictedMoveDirection(MoveDirection.NONE);
        ClientMain.getInstance().getClientMovementProcessor().setCurrentMovementInput(ClientMovementProcessor.MovementInput.NONE);

        if (abstractPostProcessor != null) {
            abstractPostProcessor.postMoveAction();
            abstractPostProcessor = null;
        }
    }
}
