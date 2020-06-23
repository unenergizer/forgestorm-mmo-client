package com.forgestorm.client.game.movement;

import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.maps.GameMap;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;

public class MoveUtil {

    static MoveDirection getMoveDirection(Location currentLocation, Location futureLocation) {
        return getMoveDirection(currentLocation.getX(), currentLocation.getY(),
                futureLocation.getX(), futureLocation.getY());
    }

    private static MoveDirection getMoveDirection(short currentX, short currentY, short futureX, short futureY) {
        if (currentX > futureX) return MoveDirection.WEST;
        else if (currentX < futureX) return MoveDirection.EAST;
        else if (currentY > futureY) return MoveDirection.SOUTH;
        else if (currentY < futureY) return MoveDirection.NORTH;
        return MoveDirection.NONE;
    }

    static Location getLocation(GameMap gameMap, MoveDirection direction) {
        if (direction == MoveDirection.SOUTH)
            return new Location(gameMap.getMapName(), (short) 0, (short) -1);
        if (direction == MoveDirection.NORTH)
            return new Location(gameMap.getMapName(), (short) 0, (short) 1);
        if (direction == MoveDirection.WEST)
            return new Location(gameMap.getMapName(), (short) -1, (short) 0);
        if (direction == MoveDirection.EAST)
            return new Location(gameMap.getMapName(), (short) 1, (short) 0);
        if (direction == MoveDirection.NONE)
            return new Location(gameMap.getMapName(), (short) 0, (short) 0);
        throw new RuntimeException("Tried to get a location, but direction could not be determined. MapName: " + gameMap.getMapName() + ", MoveDirection: " + direction);
    }

    public static boolean isEntityMoving(MovingEntity entity) {
        return entity.getCurrentMapLocation().getX() != entity.getFutureMapLocation().getX() || entity.getCurrentMapLocation().getY() != entity.getFutureMapLocation().getY();
    }

}
