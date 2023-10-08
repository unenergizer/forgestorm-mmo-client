package com.forgestorm.client.game.screens.ui.actors.dev.entity.data;

import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.maps.Location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemStackDropData extends EntityEditorData {

    private int itemStackId;
    private int amount;
    private int respawnTimeMin;
    private int respawnTimeMax;

    public ItemStackDropData(boolean spawn, boolean save, boolean delete, Location spawnLocation, short entityID) {
        super(EntityType.ITEM_STACK, spawn, save, delete, spawnLocation, entityID);
    }
}
