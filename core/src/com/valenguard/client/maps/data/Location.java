package com.valenguard.client.maps.data;

import com.valenguard.client.Valenguard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Location {

    private String mapName;
    private int x;
    private int y;

    /**
     * Helper method to quickly get the map data for this location object.
     *
     * @return The map data that relates to this location object.
     */
    public TmxMap getMapData() {
        return Valenguard.getInstance().getMapManager().getTmxMap(mapName);
    }

    public void add(int x, int y) {
        this.x = this.x + x;
        this.y = this.y + y;
    }

    public void set(Location location) {
        this.mapName = location.mapName;
        this.x = location.x;
        this.y = location.y;
    }
}
