package com.forgestorm.client.util;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.world.maps.WorldUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PathFinding {

    private final List<MoveNode> closedSet = new ArrayList<MoveNode>();
    private final List<MoveNode> openSet = new ArrayList<MoveNode>();

    private final int ALGORITHM_RADIUS = ClientConstants.CLICK_RADIUS; // Original value: 15
    private final int GRID_LENGTH = (ALGORITHM_RADIUS * 2) + 1;

    private final MoveNode[][] grid = new MoveNode[GRID_LENGTH][GRID_LENGTH];

    private List<MoveNode> currentShortestPath;

    private int calculateHeuristic(int ax, int ay, int bx, int by) {
        return Math.abs(bx - ax) + Math.abs(by - ay);
    }

    private MoveNode getCurrentNode() {
        MoveNode current = openSet.get(0);
        for (MoveNode openNode : openSet)
            if (current.getCostF() > openNode.getCostF()) current = openNode;
        return current;
    }

    private void initializeGrid(int startX, int startY, int finalX, int finalY, String worldName, boolean ignoreFinalCollision) {

        int bottomX = (startX - ALGORITHM_RADIUS);
        int bottomY = (startY - ALGORITHM_RADIUS);

        for (int i = 0; i < GRID_LENGTH; i++) {
            for (int j = 0; j < GRID_LENGTH; j++) {
                int worldX = (bottomX + i);
                int worldY = (bottomY + j);

                boolean isTraversable = WorldUtil.isTraversable(worldX, worldY);

                if (isTraversable) {
                    grid[i][j] = new MoveNode(worldX, worldY, i, j);
                } else {
                    if (ignoreFinalCollision) {
                        if (worldX == finalX && worldY == finalY) {
                            grid[i][j] = new MoveNode(worldX, worldY, i, j);
                        } else {
                            grid[i][j] = null;
                        }
                    } else {
                        grid[i][j] = null;
                    }
                }
            }
        }

        for (int i = 0; i < GRID_LENGTH; i++) {
            for (int j = 0; j < GRID_LENGTH; j++) {
                if (grid[i][j] != null) {
                    grid[i][j].setWorldName(worldName);
                    grid[i][j].addNeighbors(GRID_LENGTH, grid);
                }
            }
        }
    }

    private boolean initialConditions(int startX, int startY, int finalX, int finalY, boolean ignoreFinalCollision) {
        if (startX == finalX && startY == finalY) return false;

        boolean startTileTraversable = WorldUtil.isTraversable(startX, startY);
        boolean endTileTraversable = WorldUtil.isTraversable(finalX, finalY);

        if (!startTileTraversable) return false;
        if (!ignoreFinalCollision && !endTileTraversable) return false;
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

    public PathSolution findPath(int startX, int startY, int finalX, int finalY, String worldName, boolean ignoreFinalCollision) {
        if (!initialConditions(startX, startY, finalX, finalY, ignoreFinalCollision))
            return new PathSolution(false, new LinkedList<MoveNode>());

        MoveNode currentClosestNode = null;

        initializeGrid(startX, startY, finalX, finalY, worldName, ignoreFinalCollision);

        // Start node
        openSet.add(grid[ALGORITHM_RADIUS][ALGORITHM_RADIUS]);

        MoveNode goalNode = grid[ALGORITHM_RADIUS + finalX - startX][ALGORITHM_RADIUS + finalY - startY];

        while (!openSet.isEmpty()) {
            MoveNode current = getCurrentNode();

            if (current.equals(goalNode)) {

                List<MoveNode> pathFound = makePath(current);

                finish();
                Collections.reverse(pathFound);
                @SuppressWarnings("unchecked") Queue<MoveNode> queuePath = (Queue<MoveNode>) pathFound;
                queuePath.remove(); // Removing the node the player is standing on.
                return new PathSolution(true, queuePath);
            } else {

                if (currentClosestNode == null) {
                    currentClosestNode = current;
                    currentShortestPath = makePath(current);
                } else {
                    if (calculateHeuristic(currentClosestNode.getWorldX(), currentClosestNode.getWorldY(),
                            goalNode.getWorldX(), goalNode.getWorldY()) >
                            calculateHeuristic(current.getWorldX(), current.getWorldY(), goalNode.getWorldX(), goalNode.getWorldY())) {
                        currentClosestNode = current;
                        currentShortestPath = makePath(current);
                    }
                }
            }

            openSet.remove(current);
            closedSet.add(current);

            evaluateNeighbors(current, goalNode);
        }

        finish();


        if (currentClosestNode != null) {
            Collections.reverse(currentShortestPath);
            @SuppressWarnings("unchecked") Queue<MoveNode> queuePath = (Queue<MoveNode>) currentShortestPath;
            queuePath.remove(); // Removing the node the player is standing on.
            return new PathSolution(false, queuePath);
        }

        return new PathSolution(false, new LinkedList<MoveNode>());
    }

    private List<MoveNode> makePath(MoveNode node) {
        List<MoveNode> pathFound = new LinkedList<MoveNode>();
        MoveNode iterateNode = node;

        pathFound.add(iterateNode);
        while (iterateNode.getParentNode() != null) {
            pathFound.add(iterateNode.getParentNode());
            iterateNode = iterateNode.getParentNode();
        }
        return pathFound;
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