package com.valenguard.client.game.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;

public class EntityManager {

    private EntityManager() {}

    // TODO: REMOVE TEST CLIENT! Add the correct way.
    @Getter
    @Setter
    private PlayerClient playerClient;

    @Getter
    private final static EntityManager instance = new EntityManager();

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

}
