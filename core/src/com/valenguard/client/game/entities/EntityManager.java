package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

public class EntityManager implements Disposable {

    private EntityManager() {
    }

    // TODO: REMOVE TEST CLIENT! Add the correct way.
    @Getter
    @Setter
    private PlayerClient playerClient;

    @Getter
    private static final EntityManager instance = new EntityManager();

    //  EntityId -> Entity
    @Getter
    private Map<Short, MovingEntity> entities = new ConcurrentHashMap<Short, MovingEntity>();

    public void addEntity(short entityId, MovingEntity entity) {
        entities.put(entityId, entity);
    }

    public void removeEntity(Short entityId) {
        entities.remove(entityId);
    }

    public MovingEntity getEntity(short entityId) {
        return entities.get(entityId);
    }

    public void drawEntityBodies(float delta, SpriteBatch spriteBatch) {
        for (MovingEntity entity : entities.values()) {
            entity.getEntityAnimation().animate(delta, spriteBatch);
        }
    }

    public void drawEntityNames(float delta, SpriteBatch spriteBatch) {
        for (MovingEntity entity : entities.values()) {
            entity.drawEntityName();
        }
    }

    @Override
    public void dispose() {
        entities.clear();
        playerClient = null;
    }
}
