package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.maps.Location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityEditorItemStackData {

    private final EntityType entityType = EntityType.ITEM_STACK;

    // Editor data
    private boolean spawn;
    private boolean save;
    private boolean delete;

    // Basic data
    private short entityID;
    private int itemStackId;
    private short respawnTime;

    // World data
    private Location spawnLocation;
}
