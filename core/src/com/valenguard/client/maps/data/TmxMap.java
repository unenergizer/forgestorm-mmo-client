package com.valenguard.client.maps.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TmxMap {
    private final String mapName;
    private final int mapWidth;
    private final int mapHeight;
    private final Tile map[][];
}