package com.valenguard.client.maps.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {
    private boolean isTraversable;
    private Warp warp;
}
