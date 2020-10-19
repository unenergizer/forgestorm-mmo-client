package com.forgestorm.client.game.screens.ui.actors.dev.world;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("PointlessArithmeticExpression")
public class WangTile {

    private final Map<Integer, Integer> map48;

    private WangTile() {
        map48 = new HashMap<Integer, Integer>();
        map48.put(254, 45);
        map48.put(90, 22);
        map48.put(22, 7);
        map48.put(0, 47);
        map48.put(10, 3);
        map48.put(2, 1);
        map48.put(94, 24);
        map48.put(120, 29);
        map48.put(11, 4);
        map48.put(208, 34);
        map48.put(107, 28);
        map48.put(82, 19);
        map48.put(216, 37);
        map48.put(86, 20);
        map48.put(223, 41);
        map48.put(214, 36);
        map48.put(104, 26);
        map48.put(222, 40);
        map48.put(74, 16);
        map48.put(18, 6);
        map48.put(8, 2);
        map48.put(248, 42);
        map48.put(255, 46);
        map48.put(127, 33);
        map48.put(123, 31);
        map48.put(66, 14);
        map48.put(16, 5); // East
        map48.put(219, 39);
        map48.put(75, 17);
        map48.put(80, 18);
        map48.put(122, 30);
        map48.put(30, 11);
        map48.put(126, 32);
        map48.put(31, 12);
        map48.put(250, 43);
        map48.put(88, 21);
        map48.put(64, 13);
        map48.put(95, 25);
        map48.put(251, 44);
        map48.put(91, 23);
        map48.put(24, 8);
        map48.put(27, 10);
        map48.put(218, 38);
        map48.put(72, 15);
        map48.put(106, 27);
        map48.put(26, 9);
        map48.put(210, 35);
    }

    public static void main(String[] args) {

        int x = 0;
        int y = 1;

        //   W
        // N + S  - TODO: Figure out why the hell the array is rotated this way...
        //   E
        TestTileType[][] gameMap = {
                {new TestTileType(0), new TestTileType(1), new TestTileType(0)},
                {new TestTileType(0), new TestTileType(1), new TestTileType(0)},
                {new TestTileType(0), new TestTileType(0), new TestTileType(0)},
                {new TestTileType(0), new TestTileType(0), new TestTileType(0)},
                {new TestTileType(0), new TestTileType(0), new TestTileType(0)},
                {new TestTileType(0), new TestTileType(0), new TestTileType(0)}
        };

        WangTile wangTile = new WangTile();
        System.out.println("16x Final Value: " + wangTile.autoTile16(x, y, gameMap));
        System.out.println("48x Final Value: " + wangTile.autoTile48(x, y, gameMap));
    }

    /**
     * Returns the tile decorationType after checking all 8 positions around the tile.
     * Includes corners.
     *
     * @param x       The tile we will test for in the X axis.
     * @param y       The tile we will test for in the Y axis.
     * @param gameMap The map of the game.
     * @return The index to which tile image should be used.
     */
    private int autoTile48(int x, int y, TestTileType[][] gameMap) {
        boolean northTile, southTile, westTile, eastTile, northEastTile, northWestTile, southEastTile, southWestTile;
        int index;

        // Directional Check, including corners, returns int
        northTile = detectSameTileType(x, y - 1, gameMap);
        southTile = detectSameTileType(x, y + 1, gameMap);
        westTile = detectSameTileType(x - 1, y, gameMap);
        eastTile = detectSameTileType(x + 1, y, gameMap);
        northWestTile = detectSameTileType(x - 1, y - 1, gameMap) && westTile && northTile;
        northEastTile = detectSameTileType(x + 1, y - 1, gameMap) && northTile && eastTile;
        southWestTile = detectSameTileType(x - 1, y + 1, gameMap) && southTile && westTile;
        southEastTile = detectSameTileType(x + 1, y + 1, gameMap) && southTile && eastTile;

        // 8 bit bit masking calculation using directional check booleans values
        index = boolToInt(northWestTile) * 1
                + boolToInt(northTile) * 2
                + boolToInt(northEastTile) * 4
                + boolToInt(westTile) * 8
                + boolToInt(eastTile) * 16
                + boolToInt(southWestTile) * 32
                + boolToInt(southTile) * 64
                + boolToInt(southEastTile) * 128;

        // Take the previously calculated value and find the relevant value in the data structure to remove redundancies
        System.out.println("Index Calculated: " + index);
        index = map48.get(index);

        return index;
    }

    /**
     * This will test 4 directions and return the best auto tile.
     *
     * @param x       The tile we will test for in the X axis.
     * @param y       The tile we will test for in the Y axis.
     * @param gameMap The map of the game.
     * @return The index to which tile image should be used.
     */
    private int autoTile16(int x, int y, TestTileType[][] gameMap) {
        boolean northTile, southTile, westTile, eastTile;
        int index;

        // Directional Check
        northTile = detectSameTileType(x, y - 1, gameMap);
        southTile = detectSameTileType(x, y + 1, gameMap);
        westTile = detectSameTileType(x - 1, y, gameMap);
        eastTile = detectSameTileType(x + 1, y, gameMap);

        // 4 bit bit masking calculation using directional check booleans values
        index = boolToInt(northTile) * 1
                + boolToInt(westTile) * 2
                + boolToInt(eastTile) * 4
                + boolToInt(southTile) * 8;

        System.out.println("Index Calculated: " + index);

        return index;
    }

    /**
     * Converts a boolean to a numerical value.
     * 1 = true and 0 = false.
     *
     * @param b the boolean to test.
     * @return A 1 or a 0.
     */
    private int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    /**
     * Detect if this tile position is using the same type of tile as the main one being tested
     *
     * @param x       X axis to test.
     * @param y       Y axis to test
     * @param gameMap Map of the game.
     * @return True if the tile type is the same or False if it is not.
     */
    private boolean detectSameTileType(int x, int y, TestTileType[][] gameMap) {
        // If same type tile exist return true otherwise return false;
        if (x < 0 || x >= gameMap.length || y < 0 || y >= gameMap.length) return false; // out of bounds
        return gameMap[x][y].getTileType() == 1; // true
    }

    @Setter
    @Getter
    @AllArgsConstructor
    static class TestTileType {
        int tileType;
    }
}
