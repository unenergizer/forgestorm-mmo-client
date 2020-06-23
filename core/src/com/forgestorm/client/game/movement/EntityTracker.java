package com.forgestorm.client.game.movement;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.GameQuitReset;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.util.MoveNode;
import com.forgestorm.client.util.PathFinding;

import java.util.Queue;

public class EntityTracker implements GameQuitReset {

    private final PathFinding pathFinding = new PathFinding();
    private Location previousLocation;
    private Entity entityToTrack;

    /**
     * Sets up the {@link EntityTracker} to start tracking an entity.
     *
     * @param entity The {@link MovingEntity} we want to follow or startTracking.
     */
    public void startTracking(Entity entity) {
        entityToTrack = entity;

        // Create new location to not use MovingEntity location reference.
        previousLocation = new Location(entity.getCurrentMapLocation());
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
                Queue<MoveNode> moveNodes = pathFinding.removeLastNode(testMoveNodes);

                if (!moveNodes.isEmpty()) {
                    ClientMain.getInstance().getClientMovementProcessor().postProcessMovement(
                            new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, null));
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

    @Override
    public void gameQuitReset() {
        cancelTracking();
        pathFinding.finish();
    }
}
