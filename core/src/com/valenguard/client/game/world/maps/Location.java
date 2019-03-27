package com.valenguard.client.game.world.maps;

import com.valenguard.client.Valenguard;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("unused")
public class Location {

    private String mapName;
    private short x;
    private short y;

    public Location(String mapName, short x, short y) {
        this.mapName = mapName;
        this.x = x;
        this.y = y;
    }

    public Location(Location location) {
        this.mapName = location.mapName;
        this.x = location.x;
        this.y = location.y;
    }

    /**
     * Helper method to quickly get the map data for this location object.
     *
     * @return The map data that relates to this location object.
     */
    public GameMap getMapData() {
        return Valenguard.getInstance().getMapManager().getGameMap(mapName);
    }

    public Location add(short x, short y) {
        this.x = (short) (this.x + x);
        this.y = (short) (this.y + y);
        return this;
    }

    public Location add(Location location) {
        this.x += location.x;
        this.y += location.y;
        return this;
    }

    public Location set(Location location) {
        this.mapName = location.mapName;
        this.x = location.x;
        this.y = location.y;
        return this;
    }

    public Location set(String mapName, short tileX, short tileY) {
        this.mapName = mapName;
        this.x = tileX;
        this.y = tileY;
        return this;
    }

    public boolean isWithinDistance(Location otherLocation, short distance) {
        return getDistanceAway(otherLocation) <= distance;
    }

    public short getDistanceAway(Location otherLocation) {
        int diffX = otherLocation.getX() - x;
        int diffY = otherLocation.getY() - y;

        double realDifference = Math.sqrt((double) (diffX * diffX + diffY * diffY));
        return (short) Math.floor(realDifference);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) return false;
        Location otherLocation = (Location) obj;

        if (!otherLocation.getMapName().equals(mapName)) return false;
        if (otherLocation.getX() != x) return false;
        if (otherLocation.getY() != y) return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + mapName + "] -> [" + x + ", " + y + "]";
    }
}
