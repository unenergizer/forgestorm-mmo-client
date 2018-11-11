package com.valenguard.client.game.maps.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameMap {
    private final String mapName;
    private final int mapWidth;
    private final int mapHeight;
    private final Tile map[][];
}