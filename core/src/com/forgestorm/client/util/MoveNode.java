package com.forgestorm.client.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveNode {

    private String worldName;

    private int heuristic;
    private int costG;
    private int costF;
    private final int i;
    private final int j;

    private int worldX, worldY;

    private MoveNode parentNode;

    private MoveNode[] neighbors;

    public MoveNode(int worldX, int worldY, int i, int j) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.i = i;
        this.j = j;
    }

    @SuppressWarnings("SameParameterValue")
    void addNeighbors(int gridLength, MoveNode[][] grid) {
        int top = j + 1;
        int bottom = j - 1;
        int right = i + 1;
        int left = i - 1;

        neighbors = new MoveNode[]{
                top < gridLength && top >= 0 ? grid[i][top] : null,
                bottom < gridLength && bottom >= 0 ? grid[i][bottom] : null,
                right < gridLength && right >= 0 ? grid[right][j] : null,
                left < gridLength && left >= 0 ? grid[left][j] : null
        };
    }

    @Override
    public String toString() {
        return "x: " + worldX + " y: " + worldY;
    }
}
