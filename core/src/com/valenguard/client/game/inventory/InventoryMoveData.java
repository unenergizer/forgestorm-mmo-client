package com.valenguard.client.game.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryMoveData {
    private byte fromPosition;
    private byte toPosition;
    private byte fromWindow;
    private byte toWindow;
}
