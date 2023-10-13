package com.forgestorm.shared.game.world.maps.tile.wang;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrushSize {
    ONE(1),
    FOUR(4),
    SIX(6);

    private final int size;
}
