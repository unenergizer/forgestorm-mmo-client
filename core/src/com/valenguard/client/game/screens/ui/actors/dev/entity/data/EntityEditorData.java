package com.valenguard.client.game.screens.ui.actors.dev.entity.data;

import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.maps.Location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EntityEditorData {

    private final EntityType entityType;

    // Editor data
    private final boolean spawn;
    private final boolean save;
    private final boolean delete;

    // World data
    private final Location spawnLocation;

    // Basic data
    private final short entityID;

    public EntityEditorData(EntityType entityType, boolean spawn, boolean save, boolean delete, Location spawnLocation, short entityID) {
        this.entityType = entityType;
        this.spawn = spawn;
        this.save = save;
        this.delete = delete;
        this.spawnLocation = spawnLocation;
        this.entityID = entityID;
    }
}
