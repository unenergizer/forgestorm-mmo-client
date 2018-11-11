package com.valenguard.client.util;

import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.maps.MapUtil;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.maps.data.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.Getter;

public class PathFinding {

    private final List<MoveNode> closedSet = new ArrayList<MoveNode>();
    private final List<MoveNode> openSet = new ArrayList<MoveNode>();

    private final int ALGORITHM_RADIUS = ClientConstants.CLICK_RADIUS; // Original value: 15
    private final int GRID_LENGTH = (ALGORITHM_RADIUS * 2) + 1;

    private final MoveNode[][] grid = new MoveNode[GRID_LENGTH][GRID_LENGTH];

    private int calculateHeuristic(int ax, int ay, int bx, int by) {
        return Math.abs(bx - ax) + Math.abs(by - ay);
    }

    private MoveNode getCurrentNode() {
        MoveNode current = openSet.get(0);
        for (MoveNode openNode : openSet) if (current.getCostF() > openNode.getCostF()) current = openNode;
        return current;
    }

    private void initializeGrid(int startX, int startY, String mapName) {

        int bottomX = startX - ALGORITHM_RADIUS;
        int bottomY = startY - ALGORITHM_RADIUS;

        for (int i = 0; i < GRID_LENGTH; i++) {
            for (int j = 0; j < GRID_LENGTH; j++) {
                int worldX = bottomX + i;
                int worldY = bottomY + j;
                Tile worldTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), worldX, worldY));

                grid[i][j] = worldTile == null ? null :
                        !worldTile.isTraversable() ? null : new MoveNode(worldX, worldY, i, j);
            }
        }

        for (int i = 0; i < GRID_LENGTH; i++) {
            for (int j = 0; j < GRID_LENGTH; j++) {
                if (grid[i][j] != null) {
                    grid[i][j].setMapName(mapName);
                    grid[i][j].addNeighbors(GRID_LENGTH, grid);
                }
            }
        }
    }

    private boolean initialConditions(int startX, int startY, int finalX, int finalY) {

        if (startX == finalX && startY == finalY) return false;

        Tile startTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), startX, startY));
        Tile endTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), finalX, finalY));

        if (startTile == null || !startTile.isTraversable()) return false;

        if (endTile == null || !endTile.isTraversable()) return false;

        if (Math.abs(finalX - startX) > ALGORITHM_RADIUS || Math.abs(finalY - startY) > ALGORITHM_RADIUS) return false;

        return true;
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

    public Queue<MoveNode> findPath(int startX, int startY, int finalX, int finalY, String mapName) {

        if (!initialConditions(startX, startY, finalX, finalY)) return null;

        initializeGrid(startX, startY, mapName);

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
                Queue<MoveNode> queuePath = (Queue<MoveNode>) pathFound;
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

    private void finish() {
        closedSet.clear();
        openSet.clear();
    }
}