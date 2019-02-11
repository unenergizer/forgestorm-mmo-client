package com.valenguard.client.game.movement;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.util.MoveNode;
import com.valenguard.client.util.PathFinding;

import java.util.LinkedList;
import java.util.Queue;

public class EntityTracker {

    private final PathFinding pathFinding = new PathFinding();
    private Location previousLocation;
    private MovingEntity entityToTrack;

    /**
     * Sets up the {@link EntityTracker} to start tracking an entity.
     *
     * @param movingEntity The {@link MovingEntity} we want to follow or startTracking.
     */
    public void startTracking(MovingEntity movingEntity) {
        entityToTrack = movingEntity;

        // Create new location to not use MovingEntity location reference.
        previousLocation = new Location(movingEntity.getCurrentMapLocation());
    }

    /**
     * Tracks or follows a {@link MovingEntity}
     */
    public void track() {
        if (previousLocation == null || entityToTrack == null) return;
        if (!previousLocation.equals(entityToTrack.getCurrentMapLocation())) {
            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            Location clientLocation = playerClient.getFutureMapLocation();

            // Generate new path to the MovingEntity
            Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), entityToTrack.getCurrentMapLocation().getX(), entityToTrack.getCurrentMapLocation().getY(), clientLocation.getMapName(), false);

            if (testMoveNodes == null) {
                return;
            } else {
                // Remove one node from the node list (so we dont end on top of the MovingEntity)
                Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                    moveNodes.add(testMoveNodes.remove());
                }

                if (!moveNodes.isEmpty()) {
                    Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                            new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes));
                }
            }

            // Create new location to not use MovingEntity location reference.
            previousLocation = new Location(entityToTrack.getCurrentMapLocation());
        }
    }

    /**
     * Cancels tracking the entityToTrack
     */
    public void cancelTracking() {
        entityToTrack = null;
        previousLocation = null;
    }

}