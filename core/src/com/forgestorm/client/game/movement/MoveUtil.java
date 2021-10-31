package com.forgestorm.client.game.movement;

import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.shared.game.world.maps.MoveDirection;

public class MoveUtil {

    static MoveDirection getMoveDirection(Location currentLocation, Location futureLocation) {
        return getMoveDirection(currentLocation.getX(), currentLocation.getY(),
                futureLocation.getX(), futureLocation.getY());
    }

    private static MoveDirection getMoveDirection(int currentX, int currentY, int futureX, int futureY) {
        if (currentX > futureX) return MoveDirection.WEST;
        else if (currentX < futureX) return MoveDirection.EAST;
        else if (currentY > futureY) return MoveDirection.SOUTH;
        else if (currentY < futureY) return MoveDirection.NORTH;
        return MoveDirection.NONE;
    }

    static Location getLocation(GameWorld gameWorld, MoveDirection direction, short worldZ) {
        if (direction == MoveDirection.SOUTH)
            return new Location(gameWorld.getWorldName(), 0, -1, worldZ);
        if (direction == MoveDirection.NORTH)
            return new Location(gameWorld.getWorldName(), 0, 1, worldZ);
        if (direction == MoveDirection.WEST)
            return new Location(gameWorld.getWorldName(), -1, 0, worldZ);
        if (direction == MoveDirection.EAST)
            return new Location(gameWorld.getWorldName(), 1, 0, worldZ);
        if (direction == MoveDirection.NONE)
            return new Location(gameWorld.getWorldName(), 0, 0, worldZ);
        throw new RuntimeException("Tried to get a location, but direction could not be determined. worldName: " + gameWorld.getWorldName() + ", MoveDirection: " + direction);
    }

    public static boolean isEntityMoving(MovingEntity entity) {
        return entity.getCurrentMapLocation().getX() != entity.getFutureMapLocation().getX() || entity.getCurrentMapLocation().getY() != entity.getFutureMapLocation().getY();
    }

}
