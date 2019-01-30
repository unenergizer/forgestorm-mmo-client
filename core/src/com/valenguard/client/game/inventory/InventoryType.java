package com.valenguard.client.game.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InventoryType {

    EQUIPMENT((byte) 0),
    BAG_1((byte) 1),
    BAG_2((byte) 2),
    BAG_3((byte) 3),
    BAG_4((byte) 4);

    private byte inventoryTypeIndex;
}
