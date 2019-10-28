package com.valenguard.client.game.world.maps;

import com.badlogic.gdx.graphics.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameMap {
    private final String mapName;
    private final int mapWidth;
    private final int mapHeight;
    private final Tile[][] mapTiles;
    private final Color backgroundColor;
}