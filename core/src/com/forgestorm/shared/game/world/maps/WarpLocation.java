package com.forgestorm.shared.game.world.maps;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WarpLocation {
    private final int fromX, fromY;
    private final short fromZ;
}
