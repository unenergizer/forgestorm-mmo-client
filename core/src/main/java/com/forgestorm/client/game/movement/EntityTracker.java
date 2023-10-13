package com.forgestorm.client.game.movement;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.GameQuitReset;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.util.MoveNode;
import com.forgestorm.client.util.PathFinding;
import com.forgestorm.client.util.PathSolution;
import lombok.Setter;

import java.util.Queue;

import static com.forgestorm.client.util.Log.println;

public class EntityTracker implements GameQuitReset {

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;
    private final PathFinding pathFinding;
    private Location previousLocation;
    private Entity entityToTrack;
    private AbstractPostProcessor abstractPostProcessor;

    @Setter
    private int distanceCheck = -1;

    private enum TrackType {
        WALK_TO,
        FOLLOW
    }

    public EntityTracker(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.pathFinding = new PathFinding(clientMain);
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

    public void walkTo(int tileX, int tileY, short worldZ, boolean ignoreFileTile) {
        cancelFollow();
        walkToTileLocation(tileX, tileY, worldZ, ignoreFileTile);
    }

    public void setPostProcessor(AbstractPostProcessor abstractPostProcessor) {
        this.abstractPostProcessor = abstractPostProcessor;
    }

    private boolean walkToTileLocation(int tileX, int tileY, short worldZ, boolean ignoreFinalTile) {

        println(getClass(), "walkToTileLocation()", false, PRINT_DEBUG);

        PlayerClient playerClient = clientMain.getEntityManager().getPlayerClient();
        Location clientLocation = playerClient.getFutureMapLocation();

        PathSolution pathSolution = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), tileX, tileY, clientLocation.getWorldName(), ignoreFinalTile);

        if (pathSolution.getPath() == null || pathSolution.getPath().isEmpty()) {
            println(getClass(), "PATH WAS NULL CHECKING DISTANCE>0", false, PRINT_DEBUG);
            if (distanceCheck > 0) {
                println(getClass(), "CHECKING DISTANCE", false, PRINT_DEBUG);

                if (clientLocation.isWithinDistance(new Location(clientMain, clientLocation.getWorldName(), tileX, tileY, worldZ), distanceCheck)) {
                    if (abstractPostProcessor != null)
                        abstractPostProcessor.postMoveAction();
                    if (trackType != TrackType.FOLLOW)
                        cancelFollow();
                }
            } else {
                if (clientLocation.isWithinDistance(new Location(clientMain, clientLocation.getWorldName(), tileX, tileY, worldZ), 1)) {
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
        clientMain.getClientMovementProcessor().postProcessMovement(
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
        if (walkToTileLocation(entityLocation.getX(), entityLocation.getY(), entityLocation.getZ(), true)) {
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
    void cancelFollow() {
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
