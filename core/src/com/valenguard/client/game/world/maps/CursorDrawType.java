package com.valenguard.client.game.world.maps;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CursorDrawType {
    NO_DRAWABLE("", 0), // Default

    // Movement
    NOT_TRAVERSABLE("not_traversable", 16),
    WARP("warp_door", 16),

    // Skills
    MINING("skill_mining", 8);

    private String drawableRegion;
    private int size;
}
