package com.forgestorm.client.io;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.world.maps.GameMap;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.game.world.maps.Warp;

import java.util.HashMap;
import java.util.Map;

public class JsonMapParser {

    public static GameMap load(FileHandle fileHandle) {

        JsonValue root = new JsonReader().parse(fileHandle.reader());

        String mapName = fileHandle.name().replace(".json", "");
        int red = root.get("mapBackgroundRed").asInt();
        int green = root.get("mapBackgroundGreen").asInt();
        int blue = root.get("mapBackgroundBlue").asInt();
        int alpha = root.get("mapBackgroundAlpha").asInt();
        int mapWidth = root.get("mapWidth").asInt();
        int mapHeight = root.get("mapHeight").asInt();

        Map<Integer, TileImage[]> layers = new HashMap<Integer, TileImage[]>();

        TileImage[] layer = readLayer("layer1", root, mapWidth, mapHeight);

        layers.put(0, layer);

        GameMap gameMap = new GameMap();
        gameMap.setMapName(mapName);
        gameMap.setMapWidth(mapWidth);
        gameMap.setMapHeight(mapHeight);
        gameMap.setLayers(layers);
        gameMap.setBackgroundColor(new Color(red / 255f, green / 255f, blue / 255f, alpha));

        JsonValue warpsArray = root.get("warps");
        for (JsonValue jsonWarp = warpsArray.child; jsonWarp != null; jsonWarp = jsonWarp.next) {
            Warp warp = new Warp(
                    new Location(jsonWarp.get("toMap").asString(), jsonWarp.get("toX").asShort(), jsonWarp.get("toY").asShort()),
                    MoveDirection.valueOf(jsonWarp.get("facingDirection").asString())
            );
            gameMap.addTileWarp(jsonWarp.get("x").asShort(), jsonWarp.get("y").asShort(), warp);
        }

        return gameMap;
    }

    private static TileImage[] readLayer(String layerName, JsonValue root, int mapWidth, int mapHeight) {
        String layer = root.get(layerName).asString();
        String[] imageIds = layer.split(",");
        Map<Integer, TileImage> tileImages = ClientMain.getInstance().getWorldBuilder().getTileImageMap();
        TileImage[] tiles = new TileImage[mapWidth * mapHeight];
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                TileImage tileImage = tileImages.get(Integer.parseInt(imageIds[x + y * mapWidth]));
                tiles[x + y * mapWidth] = tileImage;
            }
        }
        return tiles;
    }


}
