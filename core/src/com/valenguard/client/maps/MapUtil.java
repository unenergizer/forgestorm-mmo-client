package com.valenguard.client.maps;

import com.valenguard.client.maps.data.Location;
import com.valenguard.client.maps.data.Tile;
import com.valenguard.client.maps.data.TmxMap;
import com.valenguard.client.maps.data.Warp;

import lombok.NonNull;

@SuppressWarnings("unused")
public class MapUtil {

    /**
     * Test to see if the tile/coordinate can be walked on.
     *
     * @param tmxMap The tmxMap of the coordinates to check.
     * @param x      The X grid coordinate a entity is attempting to playerMove to.
     * @param y      The Y grid coordinate a entity is attempting to playerMove to.
     * @return True if the tile/coordinate is walkable. False otherwise.
     */
    public static boolean isTraversable(@NonNull TmxMap tmxMap, int x, int y) {
        if (isOutOfBounds(tmxMap, x, y)) return false;
        return tmxMap.getMap()[x][y].isTraversable();
    }

    /**
     * This is a test to make sure the entity does not go outside the tmxMap.
     *
     * @param tmxMap The tmxMap of the coordinates to check.
     * @param x      The X grid coordinate a entity is attempting to playerMove to.
     * @param y      The Y grid coordinate a entity is attempting to playerMove to.
     * @return True if entity is attempting to playerMove outside the tmxMap. False otherwise.
     */
    public static boolean isOutOfBounds(@NonNull TmxMap tmxMap, int x, int y) {
        return x < 0 || x >= tmxMap.getMapWidth() || y < 0 || y >= tmxMap.getMapHeight();
    }

    public static Warp getWarp(@NonNull TmxMap tmxMap, int x, int y) {
        return tmxMap.getMap()[x][y].getWarp();
    }

    public static boolean hasWarp(@NonNull TmxMap tmxMap, int x, int y) {
        return tmxMap.getMap()[x][y].getWarp() != null;
    }

    /**
     * Retrieves a tile by the location passed in. It is assumed that the location
     * is not out of bounds before being passed.
     *
     * @param location the location on the map.
     * @return The tile associated with the location.
     */
    public static Tile getTileByLocation(@NonNull Location location) {
        if (isOutOfBounds(location.getMapData(), location.getX(), location.getY())) return null;
        return location.getMapData().getMap()[location.getX()][location.getY()];
    }
}
