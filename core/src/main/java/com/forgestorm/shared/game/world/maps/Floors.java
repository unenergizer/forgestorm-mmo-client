package com.forgestorm.shared.game.world.maps;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Floors {

    FLOOR_5((short) 4, "Floor 5"),
    FLOOR_4((short) 3, "Floor 4"),
    FLOOR_3((short) 2, "Floor 3"),
    FLOOR_2((short) 1, "Floor 2"),
    GROUND_FLOOR((short) 0, "Ground Level"),
    UNDERGROUND_1((short) -1, "Underground 1"),
    UNDERGROUND_2((short) -2, "Underground 2"),
    UNDERGROUND_3((short) -3, "Underground 3");

    private final short worldZ;

    private final String name;

    public static Floors getHighestFloor() {
        return Floors.FLOOR_5;
    }

    public static Floors getLowestFloor() {
        return Floors.UNDERGROUND_3;
    }

    public static Floors getFloor(short worldZ) {
        for (Floors floor : Floors.values()) {
            if (floor.getWorldZ() == worldZ) return floor;
        }
        return null;
    }
}
