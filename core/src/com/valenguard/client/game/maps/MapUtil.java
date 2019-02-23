package com.valenguard.client.game.maps;


import com.valenguard.client.game.maps.data.GameMap;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.maps.data.Tile;

@SuppressWarnings("unused")
public class MapUtil {

    /**
     * Test to see if the tile/coordinate can be walked on.
     *
     * @param gameMap The gameMap of the coordinates to check.
     * @param x       The X grid coordinate a entity is attempting to playerMove to.
     * @param y       The Y grid coordinate a entity is attempting to playerMove to.
     * @return True if the tile/coordinate is walkable. False otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isTraversable(GameMap gameMap, short x, short y) {
        if (isOutOfBounds(gameMap, x, y)) return false;
        return gameMap.getMap()[x][y].isFlagSet(Tile.TRAVERSABLE);
    }

    /**
     * This is a test to make sure the entity does not go outside the gameMap.
     *
     * @param gameMap The gameMap of the coordinates to check.
     * @param x       The X grid coordinate a entity is attempting to playerMove to.
     * @param y       The Y grid coordinate a entity is attempting to playerMove to.
     * @return True if entity is attempting to playerMove outside the gameMap. False otherwise.
     */
    public static boolean isOutOfBounds(GameMap gameMap, short x, short y) {
        return x < 0 || x >= gameMap.getMapWidth() || y < 0 || y >= gameMap.getMapHeight();
    }

    public static boolean isWarp(GameMap gameMap, short x, short y) {
        return gameMap.getMap()[x][y].isFlagSet(Tile.WARP);
    }

    /**
     * Retrieves a tile by the location passed in. It is assumed that the location
     * is not out of bounds before being passed.
     *
     * @param location the location on the map.
     * @return The tile associated with the location.
     */
    public static Tile getTileByLocation(Location location) {
        if (isOutOfBounds(location.getMapData(), location.getX(), location.getY())) return null;
        return location.getMapData().getMap()[location.getX()][location.getY()];
    }
}
