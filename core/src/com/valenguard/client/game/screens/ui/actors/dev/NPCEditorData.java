package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.game.world.maps.Location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NPCEditorData {

    // Editor data
    private boolean spawn;
    private boolean save;

    // Basic data
    private short entityID;
    private String name;
    private String faction;
    private int health;
    private int damage;
    private int expDrop;
    private int dropTable;
    private float walkSpeed;
    private float probStop;
    private float probWalk;
    private short shopId;
    private boolean bankKeeper;

    // World data
    private Location spawnLocation;

    // Appearance
    private byte hairTexture;
    private byte helmTexture;
    private byte chestTexture;
    private byte pantsTexture;
    private byte shoesTexture;
    private Color hairColor;
    private Color eyesColor;
    private Color skinColor;
    private Color glovesColor;
}
