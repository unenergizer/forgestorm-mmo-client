package com.valenguard.client.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // EntityType -> EntityId -> Entity
    private Map<Class<? extends Entity>, Map<Short, Entity>> entities = new HashMap<Class<? extends Entity>, Map<Short, Entity>>();

    @SuppressWarnings("unchecked")
    public <T extends Entity> Map<Short, T> getEntitiesMap(Class<? extends Entity> entityType) {
        Map<Short, T> entityOfType = (Map<Short, T>) entities.get(entityType);
        if (entityOfType == null) entities.put(entityType, new HashMap<Short, Entity>());
        return (Map<Short, T>) entities.get(entityType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> getEntities(Class<? extends Entity> entityType) {
        return new ArrayList(getEntitiesMap(entityType).values());
    }

    public <T extends Entity> void addEntity(Class<? extends Entity> entityType, short entityId, T entity) {
        getEntitiesMap(entityType).put(entityId, entity);
    }

    public void removeEntity(Class<? extends Entity> entityType, short entityId) {
        getEntitiesMap(entityType).remove(entityId);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> T getEntity(Class<? extends Entity> entityType, short entityId) {
        return (T) getEntitiesMap(entityType).get(entityId);
    }

}
