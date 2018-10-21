package com.valenguard.client.entities;

import com.valenguard.client.maps.data.Location;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Entity {

    private static final String TAG = Entity.class.getSimpleName();

    /**
     * A unique ID given to this entity by the server.
     */
    private int entityId;

    /**
     * The map this entity was last seen on.
     */
    private String mapName;

    /**
     * The exact tile location of the entity on the tile grid.
     */
    private Location currentMapLocation, futureMapLocation;

    /**
     * The actual sprite position on the screen.
     */
    private float drawX, drawY;

    /**
     * The current direction the entity is moving in.
     */
    private Direction moveDirection;

    /**
     * The direction the entity intends to move in the future.
     */
    private Direction predictedDirection;


    /**
     * The direction the entity is facing. Is not always the same direction
     * as they are moving because the move direction can be STOP.
     */
    private Direction facingDirection;

    public boolean isMoving() {
        return moveDirection != Direction.STOP;
    }
}
