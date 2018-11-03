package com.valenguard.client.entities;

import com.valenguard.client.Valenguard;
import com.valenguard.client.maps.data.Location;
import com.valenguard.client.maps.data.TmxMap;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Entity {

    private static final String TAG = Entity.class.getSimpleName();

    /**
     * The display name of this entity.
     */
    private String entityName;

    /**
     * A unique ID given to this entity by the server.
     */
    private int serverEntityID;

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
     * The direction the entity is facing. Is not always the same direction
     * as they are moving because the move direction can be NONE.
     */
    private MoveDirection facingMoveDirection;

    private float moveSpeed;

    private float walkTime = 0;

    private Queue<Location> futureLocationRequests = new LinkedList<Location>();

    public void addLocationToFutureQueue(Location location) {
        futureLocationRequests.add(location);
    }

    public TmxMap getTmxMap() {
        return Valenguard.getInstance().getMapManager().getTmxMap(mapName);
    }
}
