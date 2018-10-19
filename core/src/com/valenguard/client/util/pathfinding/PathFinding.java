package com.valenguard.client.util.pathfinding;

import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.maps.MapUtil;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.maps.data.Tile;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class PathFinding {

    private List<MoveNode> closedSet = new ArrayList<MoveNode>();
    private List<MoveNode> openSet = new ArrayList<MoveNode>();

    private final int ALGORITHM_RADIUS = 15;
    private final int GRID_LENGTH = (ALGORITHM_RADIUS * 2) + 1;

    private MoveNode[][] grid = new MoveNode[GRID_LENGTH][GRID_LENGTH];

    private int calculateHeuristic(int ax, int ay, int bx, int by) {
        return Math.abs(bx - ax) + Math.abs(by - ay);
    }

    private MoveNode getCurrentNode() {
        MoveNode current = openSet.get(0);
        for (MoveNode openNode : openSet) if (current.costF > openNode.costF) current = openNode;
        return current;
    }

    private void initializeGrid(int startX, int startY) {

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
                if (grid[i][j] != null) grid[i][j].addNeighbors();
            }
        }
    }

    private boolean initialConditions(int startX, int startY, int finalX, int finalY) {

        if (startX == finalX && startY == finalY) {
            return false;
        }

        Tile startTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), startX, startY));
        Tile endTile = MapUtil.getTileByLocation(new Location(EntityManager.getInstance().getPlayerClient().getMapName(), finalX, finalY));

        if (startTile == null || !startTile.isTraversable()) {
            return false;
        }

        if (endTile == null || !endTile.isTraversable()) {
            System.out.println("The end tile may not be collidable.");
            return false;
        }

        if (Math.abs(finalX - startX) > ALGORITHM_RADIUS || Math.abs(finalY - startY) > ALGORITHM_RADIUS) {
            System.out.println("Attempting a move outside of the algorithm's radius.");
            return false;
        }

        return true;
    }

    private void evaluateNeighbors(MoveNode current, MoveNode goalNode) {

        for (MoveNode neighbor : current.neighbors) {
            if (neighbor == null) continue;

            if (!closedSet.contains(neighbor)) {
                int attemptG = neighbor.costG + 1;

                if (openSet.contains(neighbor)) {
                    if (attemptG < neighbor.costG) neighbor.costG = attemptG;
                } else {
                    neighbor.costG = attemptG;
                    openSet.add(neighbor);
                }

                neighbor.heuristic = calculateHeuristic(neighbor.i, neighbor.j, goalNode.i, goalNode.j);
                neighbor.costF = neighbor.heuristic + neighbor.costG;
                neighbor.parentNode = current;
            }
        }
    }

    public List<MoveNode> findPath(int startX, int startY, int finalX, int finalY) {

        if (!initialConditions(startX, startY, finalX, finalY)) return null;

        initializeGrid(startX, startY);

        // Start node
        openSet.add(grid[ALGORITHM_RADIUS][ALGORITHM_RADIUS]);

        MoveNode goalNode = grid[ALGORITHM_RADIUS + finalX - startX][ALGORITHM_RADIUS + finalY - startY];

        while (!openSet.isEmpty()) {
            MoveNode current = getCurrentNode();

            if (current.equals(goalNode)) {

                List<MoveNode> pathFound = new ArrayList<MoveNode>();
                MoveNode iterateNode = current;

                pathFound.add(iterateNode);
                while (iterateNode.parentNode != null) {
                    pathFound.add(iterateNode.parentNode);
                    iterateNode = iterateNode.parentNode;
                }

                finish();
                return pathFound;
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

    public class MoveNode {

        private int heuristic;
        private int costG;
        private int costF;
        private int i, j;

        private @Getter
        int worldX, worldY;

        private MoveNode parentNode;

        private MoveNode[] neighbors;

        private MoveNode(int worldX, int worldY, int i, int j) {
            this.worldX = worldX;
            this.worldY = worldY;
            this.i = i;
            this.j = j;
        }

        private void addNeighbors() {
            int top = j + 1;
            int bottom = j - 1;
            int right = i + 1;
            int left = i - 1;

            neighbors = new MoveNode[]{
                    top < GRID_LENGTH && top >= 0 ? grid[i][top] : null,
                    bottom < GRID_LENGTH && bottom >= 0 ? grid[i][bottom] : null,
                    right < GRID_LENGTH && right >= 0 ? grid[right][j] : null,
                    left < GRID_LENGTH && left >= 0 ? grid[left][j] : null
            };
        }

        @Override
        public String toString() {
            return "x: " + worldX + " y: " + worldY;
        }
    }
}