package com.forgestorm.client.game.world.maps;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.shared.game.world.maps.MoveDirection;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("unused")
public class Location {

    private final ClientMain clientMain;
    private String worldName;
    private int x;
    private int y;
    private short z;

    public Location(ClientMain clientMain, String worldName, int x, int y, short z) {
        this.clientMain = clientMain;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(Location location) {
        this.clientMain = location.clientMain;
        this.worldName = location.worldName;
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
    }

    /**
     * Helper method to quickly get the map data for this location object.
     *
     * @return The map data that relates to this location object.
     */
    public GameWorld getGameWorld() {
        return clientMain.getWorldManager().getGameWorld(worldName);
    }

    public WorldChunk getLocationChunk() {
        return getGameWorld().findChunk(x, y);
    }

    public Location add(int x, int y) {
        this.x = this.x + x;
        this.y = this.y + y;
        return this;
    }

    public Location add(Location location) {
        this.x += location.x;
        this.y += location.y;
        return this;
    }

    public Location add(MoveDirection direction) {
        switch (direction) {
            case NORTH:
                this.y += 1;
                break;
            case EAST:
                this.x += 1;
                break;
            case SOUTH:
                this.y -= 1;
                break;
            case WEST:
                this.x -= 1;
                break;
            case NONE:
                throw new RuntimeException("Why?");
        }
        return this;
    }

    public Location set(Location location) {
        this.worldName = location.worldName;
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        return this;
    }

    public Location set(String worldName, int tileX, int tileY, short worldZ) {
        this.worldName = worldName;
        this.x = tileX;
        this.y = tileY;
        this.z = worldZ;
        return this;
    }

    public boolean isWithinDistance(Entity entity, int distance) {
        return isWithinDistance(entity.getCurrentMapLocation(), distance);
    }

    public boolean isWithinDistance(Location otherLocation, int distance) {
        return getDistanceAway(otherLocation) <= distance;
    }

    public int getDistanceAway(Location otherLocation) {
        int diffX = otherLocation.getX() - x;
        int diffY = otherLocation.getY() - y;

        double realDifference = Math.sqrt(diffX * diffX + diffY * diffY);
        return (int) Math.floor(realDifference);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) return false;
        Location otherLocation = (Location) obj;

        if (!otherLocation.getWorldName().equals(worldName)) return false;
        if (otherLocation.getX() != x) return false;
        if (otherLocation.getY() != y) return false;
        if (otherLocation.getZ() != z) return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + worldName + "] -> [" + x + ", " + y + ", " + z + "]";
    }
}
