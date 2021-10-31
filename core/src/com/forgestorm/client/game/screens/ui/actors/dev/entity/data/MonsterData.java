package com.forgestorm.client.game.screens.ui.actors.dev.entity.data;

import com.forgestorm.client.game.rpg.EntityAlignment;
import com.forgestorm.client.game.world.entities.EntityType;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.shared.game.world.entities.FirstInteraction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonsterData extends EntityEditorData {

    private String name;
    private FirstInteraction firstInteraction;
    private String faction;
    private EntityAlignment entityAlignment;
    private int health;
    private int damage;
    private int expDrop;
    private int dropTable;
    private float walkSpeed;
    private float probStop;
    private float probWalk;
    private short shopId;
    private boolean bankKeeper;

    // Monster Appearance
    private byte singleBodyTexture;

    public MonsterData(boolean spawn, boolean save, boolean delete, Location spawnLocation, short entityID) {
        super(EntityType.MONSTER, spawn, save, delete, spawnLocation, entityID);
    }
}
