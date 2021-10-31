package com.forgestorm.shared.game.world.maps;

import com.forgestorm.client.game.world.maps.Location;

import lombok.Getter;

@Getter
public class Warp {
    private final Location warpDestination;
    private final MoveDirection directionToFace;

    private int fromX, fromY;
    private short fromZ;

    public Warp(Location warpDestination, MoveDirection directionToFace) {
        this.warpDestination = warpDestination;
        this.directionToFace = directionToFace;
    }

    public Warp(Location warpDestination, MoveDirection directionToFace, int fromX, int fromY, short fromZ) {
        this.warpDestination = warpDestination;
        this.directionToFace = directionToFace;
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromZ = fromZ;
    }
}
