package com.forgestorm.client.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveNode {

    private String mapName;

    private int heuristic;
    private int costG;
    private int costF;
    private final short i;
    private final short j;

    private short worldX, worldY;

    private MoveNode parentNode;

    private MoveNode[] neighbors;

    public MoveNode(short worldX, short worldY, short i, short j) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.i = i;
        this.j = j;
    }

    void addNeighbors(short GRID_LENGTH, MoveNode[][] grid) {
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
