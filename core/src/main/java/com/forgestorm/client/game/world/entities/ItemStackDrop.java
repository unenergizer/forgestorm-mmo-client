package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientMain;
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

    public ItemStackDrop(ClientMain clientMain) {
        super(clientMain);
    }
}
