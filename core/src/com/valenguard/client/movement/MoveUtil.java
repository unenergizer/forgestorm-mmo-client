package com.valenguard.client.movement;

import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.MoveDirection;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.maps.data.TmxMap;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MoveUtil {

    public static MoveDirection getMoveDirection(Location currentLocation, Location futureLocation) {
        return getMoveDirection(currentLocation.getX(), currentLocation.getY(),
                futureLocation.getX(), futureLocation.getY());
    }

    public static MoveDirection getMoveDirection(int currentX, int currentY, int futureX, int futureY) {
        if (currentX > futureX) return MoveDirection.LEFT;
        else if (currentX < futureX) return MoveDirection.RIGHT;
        else if (currentY > futureY) return MoveDirection.DOWN;
        else if (currentY < futureY) return MoveDirection.UP;
        return MoveDirection.NONE;
    }

    public static Location getLocation(TmxMap tmxMap, MoveDirection direction) {
        if (direction == MoveDirection.DOWN) return new Location(tmxMap.getMapName(), 0, -1);
        if (direction == MoveDirection.UP) return new Location(tmxMap.getMapName(), 0, 1);
        if (direction == MoveDirection.LEFT) return new Location(tmxMap.getMapName(), -1, 0);
        if (direction == MoveDirection.RIGHT) return new Location(tmxMap.getMapName(), 1, 0);
        if (direction == MoveDirection.NONE) return new Location(tmxMap.getMapName(), 0, 0);
        throw new RuntimeException("Tried to get a location, but direction could not be determined. MapName: " + tmxMap.getMapName() + ", MoveDirection: " + direction);
    }

    public static boolean isEntityMoving(Entity entity) {
        return entity.getCurrentMapLocation().getX() != entity.getFutureMapLocation().getX() || entity.getCurrentMapLocation().getY() != entity.getFutureMapLocation().getY();
    }

}
