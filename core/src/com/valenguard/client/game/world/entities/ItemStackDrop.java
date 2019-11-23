package com.valenguard.client.game.world.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemStackDrop extends Entity {
    private boolean spawnedFromDropTable;
    private int itemStackId;
    private int stackSize;
    private int respawnTimeMin;
    private int respawnTimeMax;
}
