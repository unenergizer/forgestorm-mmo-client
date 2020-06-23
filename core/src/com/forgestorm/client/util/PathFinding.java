package com.forgestorm.client.util;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MapUtil;
import com.forgestorm.client.game.world.maps.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.forgestorm.client.util.Log.println;

public class PathFinding {

    private static final boolean PRINT_DEBUG = false;

    private final List<MoveNode> closedSet = new ArrayList<MoveNode>();
    private final List<MoveNode> openSet = new ArrayList<MoveNode>();

    private final short ALGORITHM_RADIUS = ClientConstants.CLICK_RADIUS; // Original value: 15
    private final short GRID_LENGTH = (ALGORITHM_RADIUS * 2) + 1;

    private final MoveNode[][] grid = new MoveNode[GRID_LENGTH][GRID_LENGTH];

    private int calculateHeuristic(int ax, int ay, int bx, int by) {
        return Math.abs(bx - ax) + Math.abs(by - ay);
    }

    private MoveNode getCurrentNode() {
        MoveNode current = openSet.get(0);
        for (MoveNode openNode : openSet)
            if (current.getCostF() > openNode.getCostF()) current = openNode;
        return current;
    }

    private void initializeGrid(short startX, short startY, short finalX, short finalY, String mapName, boolean ignoreFinalCollision) {

        short bottomX = (short) (startX - ALGORITHM_RADIUS);
        short bottomY = (short) (startY - ALGORITHM_RADIUS);

        for (short i = 0; i < GRID_LENGTH; i++) {
            for (short j = 0; j < GRID_LENGTH; j++) {
                short worldX = (short) (bottomX + i);
                short worldY = (short) (bottomY + j);
                Tile worldTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), worldX, worldY));

                if (worldTile == null) {
                    grid[i][j] = null;
                } else if (!worldTile.isFlagSet(Tile.TRAVERSABLE)) {
                    if (ignoreFinalCollision) {
                        if (worldX == finalX && worldY == finalY) {
                            println(getClass(), "FINAL [X,Y] = " + "[" + worldX + ", " + worldY + "]", false, PRINT_DEBUG);
                            grid[i][j] = new MoveNode(worldX, worldY, i, j);
                        } else {
                            grid[i][j] = null;
                        }
                    } else {
                        grid[i][j] = null;
                    }
                } else {
                    grid[i][j] = new MoveNode(worldX, worldY, i, j);
                }
            }
        }

        for (short i = 0; i < GRID_LENGTH; i++) {
            for (short j = 0; j < GRID_LENGTH; j++) {
                if (grid[i][j] != null) {
                    grid[i][j].setMapName(mapName);
                    grid[i][j].addNeighbors(GRID_LENGTH, grid);
                }
            }
        }
    }

    private boolean initialConditions(short startX, short startY, short finalX, short finalY, boolean ignoreFinalCollision) {
        if (startX == finalX && startY == finalY) return false;

        Tile startTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), startX, startY));
        Tile endTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), finalX, finalY));

        if (startTile == null || !startTile.isFlagSet(Tile.TRAVERSABLE)) return false;
        if (endTile == null) return false;
        if (!ignoreFinalCollision && !endTile.isFlagSet(Tile.TRAVERSABLE)) return false;
        return Math.abs(finalX - startX) <= ALGORITHM_RADIUS && Math.abs(finalY - startY) <= ALGORITHM_RADIUS;
    }

    private void evaluateNeighbors(MoveNode current, MoveNode goalNode) {
        for (MoveNode neighbor : current.getNeighbors()) {
            if (neighbor == null) continue;

            if (!closedSet.contains(neighbor)) {
                int attemptG = neighbor.getCostG() + 1;

                if (openSet.contains(neighbor)) {
                    if (attemptG < neighbor.getCostG()) neighbor.setCostG(attemptG);
                } else {
                    neighbor.setCostG(attemptG);
                    openSet.add(neighbor);
                }

                neighbor.setHeuristic(calculateHeuristic(neighbor.getI(), neighbor.getJ(), goalNode.getI(), goalNode.getJ()));
                neighbor.setCostF(neighbor.getHeuristic() + neighbor.getCostG());
                neighbor.setParentNode(current);
            }
        }
    }

    public Queue<MoveNode> findPath(short startX, short startY, short finalX, short finalY, String mapName, boolean ignoreFinalCollision) {
        if (!initialConditions(startX, startY, finalX, finalY, ignoreFinalCollision)) return null;

        initializeGrid(startX, startY, finalX, finalY, mapName, ignoreFinalCollision);

        // Start node
        openSet.add(grid[ALGORITHM_RADIUS][ALGORITHM_RADIUS]);

        MoveNode goalNode = grid[ALGORITHM_RADIUS + finalX - startX][ALGORITHM_RADIUS + finalY - startY];

        while (!openSet.isEmpty()) {
            MoveNode current = getCurrentNode();

            if (current.equals(goalNode)) {

                List<MoveNode> pathFound = new LinkedList<MoveNode>();
                MoveNode iterateNode = current;

                pathFound.add(iterateNode);
                while (iterateNode.getParentNode() != null) {
                    pathFound.add(iterateNode.getParentNode());
                    iterateNode = iterateNode.getParentNode();
                }

                finish();
                Collections.reverse(pathFound);
                @SuppressWarnings("unchecked") Queue<MoveNode> queuePath = (Queue<MoveNode>) pathFound;
                queuePath.remove(); // Removing the node the player is standing on.
                return queuePath;
            }

            openSet.remove(current);
            closedSet.add(current);

            evaluateNeighbors(current, goalNode);
        }

        finish();
        return null;
    }

    public Queue<MoveNode> removeLastNode(Queue<MoveNode> testMoveNodes) {
        Queue<MoveNode> moveNodes = new LinkedList<MoveNode>();
        for (int i = testMoveNodes.size() - 1; i > 0; i--) {
            moveNodes.add(testMoveNodes.remove());
        }
        return moveNodes;
    }

    public void finish() {
        closedSet.clear();
        openSet.clear();
    }
}