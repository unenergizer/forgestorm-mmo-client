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

import static com.valenguard.client.util.Log.println;

public class EntityTracker {

    private final PathFinding pathFinding = new PathFinding();
    private Location previousLocation;
    private MovingEntity entityToTrack;

    public void track(MovingEntity movingEntity) {
        entityToTrack = movingEntity;
        previousLocation = new Location(movingEntity.getCurrentMapLocation());
    }

    public void tick() {
        if (previousLocation == null || entityToTrack == null) return;
        if (!previousLocation.equals(entityToTrack.getCurrentMapLocation())) {

            println(getClass(), "EntityTracking finding new path");

            PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
            Location clientLocation = playerClient.getFutureMapLocation();

            Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), entityToTrack.getCurrentMapLocation().getX(), entityToTrack.getCurrentMapLocation().getY(), clientLocation.getMapName(), false);

            if (testMoveNodes == null) {
                return;
            } else {
                Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
                for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                    moveNodes.add(testMoveNodes.remove());
                }

                if (!moveNodes.isEmpty()) {
                    Valenguard.getInstance().getClientMovementProcessor().preProcessMovement(
                            new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes));
                }
            }

            previousLocation = new Location(entityToTrack.getCurrentMapLocation());
        }
    }

    public void cancel() {
        entityToTrack = null;
        previousLocation = null;
        println(getClass(), "EntityTracking canceled");
    }

}
