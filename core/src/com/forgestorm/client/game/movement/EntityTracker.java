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
import com.forgestorm.client.util.PathSolution;

import java.util.Queue;

import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class EntityTracker implements GameQuitReset {

    private static final boolean PRINT_DEBUG = false;

    private final PathFinding pathFinding = new PathFinding();
    private Location previousLocation;
    private Entity entityToTrack;
    private AbstractPostProcessor abstractPostProcessor;

    @Setter
    private short distanceCheck = -1;

    private enum TrackType {
        WALK_TO,
        FOLLOW
    }

    private TrackType trackType = TrackType.FOLLOW;

    /**
     * Sets up the {@link EntityTracker} to start tracking an entity.
     *
     * @param entity The {@link MovingEntity} we want to follow or follow.
     */
    public void follow(Entity entity) {
        trackType = TrackType.FOLLOW;
        setup(entity);
    }

    public void walkTo(Entity entity) {
        trackType = TrackType.WALK_TO;
        setup(entity);
    }

    public void walkTo(short tileX, short tileY, boolean ignoreFileTile) {
        cancelFollow();
        walkToTileLocation(tileX, tileY, ignoreFileTile);
    }

    public void setPostProcessor(AbstractPostProcessor abstractPostProcessor) {
        this.abstractPostProcessor = abstractPostProcessor;
    }

    private boolean walkToTileLocation(short tileX, short tileY, boolean ignoreFinalTile) {

        println(getClass(), "walkToTileLocation()", false, PRINT_DEBUG);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Location clientLocation = playerClient.getFutureMapLocation();

        PathSolution pathSolution = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), tileX, tileY, clientLocation.getMapName(), ignoreFinalTile);

        if (pathSolution.getPath() == null || pathSolution.getPath().isEmpty()) {
            println(getClass(), "PATH WAS NULL CHECKING DISTANCE>0", false, PRINT_DEBUG);
            if (distanceCheck > 0) {
                println(getClass(), "CHECKING DISTANCE", false, PRINT_DEBUG);

                if (clientLocation.isWithinDistance(new Location(clientLocation.getMapName(), tileX, tileY), distanceCheck)) {
                    if (abstractPostProcessor != null)
                        abstractPostProcessor.postMoveAction();
                    if (trackType != TrackType.FOLLOW)
                        cancelFollow();
                }
            } else {
                if (clientLocation.isWithinDistance(new Location(clientLocation.getMapName(), tileX, tileY), (short) 1)) {
                    if (abstractPostProcessor != null)
                        abstractPostProcessor.postMoveAction();
                    if (trackType != TrackType.FOLLOW)
                        cancelFollow();
                }
            }
            return true;
        } else {
            println(getClass(), "PATH NOT EMPTY", false, PRINT_DEBUG);
            println(getClass(), "" + pathSolution.getPath().peek(), false, PRINT_DEBUG);
        }

        Queue<MoveNode> moveNodes;
        if (ignoreFinalTile && !(!pathSolution.isFoundGoal() && distanceCheck > 0)) {
            moveNodes = pathFinding.removeLastNode(pathSolution.getPath());
        } else {
            moveNodes = pathSolution.getPath();
        }

        if (moveNodes.isEmpty()) {
            if (abstractPostProcessor != null)
                abstractPostProcessor.postMoveAction();
            if (trackType != TrackType.FOLLOW)
                cancelFollow();
            return true;
        }

        println(getClass(), "ABSTRACT POST PROCESSOR = " + abstractPostProcessor, false, PRINT_DEBUG);
        ClientMain.getInstance().getClientMovementProcessor().postProcessMovement(
                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, abstractPostProcessor));

        return false;
    }

    private void setup(Entity entity) {
        entityToTrack = entity;

        // Create new location to not use MovingEntity location reference.
        previousLocation = new Location(entity.getCurrentMapLocation());

        abstractPostProcessor = null;
        distanceCheck = -1;
    }

    private boolean continueToEntity() {
        if (previousLocation == null || entityToTrack == null) return false;

        Location entityLocation = entityToTrack.getCurrentMapLocation();
        if (walkToTileLocation(entityLocation.getX(), entityLocation.getY(), true)) {
            return false;
        }

        // Create new location to not use MovingEntity location reference.
        previousLocation = new Location(entityToTrack.getCurrentMapLocation());

        return true;
    }

    /**
     * Tracks or follows a {@link MovingEntity}
     */
    public void followTick() {
        if (trackType != TrackType.FOLLOW) return;
        continueToEntity();
    }

    public void walkToTick() {
        if (trackType != TrackType.WALK_TO) return;
//        println(getClass(), "walkToTick()", false, PRINT_DEBUG);
        boolean arrived = continueToEntity();
        // Check if arrived
        if (arrived) {
            cancelFollow();
        }
    }

    /**
     * Cancels tracking the entityToTrack
     */
    public void cancelFollow() {
        println(getClass(), "==== CANCEL FOLLOW ====", false, PRINT_DEBUG);
        entityToTrack = null;
        previousLocation = null;
        abstractPostProcessor = null;
        distanceCheck = -1;
    }

    @Override
    public void gameQuitReset() {
        cancelFollow();
        pathFinding.finish();
    }
}
