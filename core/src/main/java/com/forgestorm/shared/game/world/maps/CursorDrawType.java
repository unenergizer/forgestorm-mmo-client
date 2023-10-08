package com.forgestorm.shared.game.world.maps;

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
    // Tree cutting -> Axe Icon
    // Fishing -> Net or Fishing Rod Icon

    // Combat -> Sword Icon
    // NPC talk -> Speech Bubble Icon
    // NPC Shop -> Bag Icon
    // Bank -> Money Icon
    // Pickup Item -> Hand Icon

    private final String drawableRegion;
    private final int size;
}
