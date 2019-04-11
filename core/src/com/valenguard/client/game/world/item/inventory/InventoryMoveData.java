package com.valenguard.client.game.world.item.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryMoveData {
    private byte fromPosition;
    private byte toPosition;
    private byte fromWindow;
    private byte toWindow;

    private boolean isStacking;
    private int addedAmount;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InventoryMoveData)) return false;
        InventoryMoveData other = (InventoryMoveData) o;
        return other.fromPosition == this.fromPosition
                && other.toPosition == this.toPosition
                && other.fromWindow == this.fromWindow
                && other.toWindow == this.toWindow;
    }
}
