package com.valenguard.client.game.world.maps;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Warp {
    private Location location;
    private MoveDirection moveDirectionToFace;
}
