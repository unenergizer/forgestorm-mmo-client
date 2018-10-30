package com.valenguard.client.maps.data;

import com.valenguard.client.entities.MoveDirection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Warp {
    private String destinationMapName;
    private int x;
    private int y;
    private MoveDirection moveDirectionToFace;
}
