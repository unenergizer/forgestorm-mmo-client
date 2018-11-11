package com.valenguard.client.game.maps.data;

import com.valenguard.client.game.maps.MoveDirection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Warp {
    private Location location;
    private MoveDirection moveDirectionToFace;
}
