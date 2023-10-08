package com.forgestorm.shared.game.world.maps.tile.wang;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BrushSize {
    ONE(1),
    FOUR(4),
    SIX(6);

    @Getter
    private final int size;
}
