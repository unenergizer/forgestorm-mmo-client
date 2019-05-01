package com.valenguard.client.game.world.entities;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.maps.GameMap;
import com.valenguard.client.game.world.maps.Location;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Entity {

    private EntityType entityType;

    /**
     * A unique ID given to this entity by the server.
     */
    private short serverEntityID;

    /**
     * The display name of this entity.
     */
    private String entityName;

    /**
     * Spawn location of the entity
     */
    private Location currentMapLocation;

    /**
     * The map this entity was last seen on.
     */
    private String mapName;

    /**
     * The actual sprite position on the screen.
     */
    private float drawX, drawY;

    /**
     * The appearance of the entity.
     */
    private Appearance appearance;

    public GameMap getGameMap() {
        return Valenguard.getInstance().getMapManager().getGameMap(mapName);
    }
}