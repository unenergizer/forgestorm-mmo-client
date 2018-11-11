package com.valenguard.client.game.entities;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.maps.data.GameMap;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Entity {

    private Map<Class<?>, Object> attributes = new HashMap<Class<?>, Object>();

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
     * The actual sprite position on the screen.
     */
    private float drawX, drawY;

    public GameMap getGameMap() {
        return Valenguard.getInstance().getMapManager().getGameMap(mapName);
    }
}
