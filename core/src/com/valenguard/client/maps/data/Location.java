package com.valenguard.client.maps.data;

import com.valenguard.client.Valenguard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("unused")
public class Location {

    private String mapName;
    private int x;
    private int y;

    public Location(String mapName, int x, int y) {
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
    public TmxMap getMapData() {
        return Valenguard.getInstance().getMapManager().getTmxMap(mapName);
    }

    public Location add(int x, int y) {
        System.out.println("Location.add(int, int)");
        this.x = this.x + x;
        this.y = this.y + y;
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

    public Location set(String mapName, int tileX, int tileY) {
        this.mapName = mapName;
        this.x = tileX;
        this.y = tileY;
        return this;
    }
}
