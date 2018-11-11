package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.maps.data.GameMap;
import com.valenguard.client.game.movement.MoveUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Entity {

    private static final String TAG = Entity.class.getSimpleName();

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

    public GameMap getTmxMap() {
        return Valenguard.getInstance().getMapManager().getTmxMap(mapName);
    }
}
