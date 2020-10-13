package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileLayers;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileType;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class TilePropertiesLoader {

    private static final boolean PRINT_DEBUG = true;

    public Map<Integer, TileImage> loadTileProperties() {

        println(getClass(), "====== START LOADING TILES ======", false, PRINT_DEBUG);

        FileHandle fileHandle = Gdx.files.internal(FilePaths.TILES.getFilePath());
        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(fileHandle.read());

        Map<Integer, TileImage> worldImageMap = new HashMap<Integer, TileImage>();

        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            Map<String, Object> itemNode = entry.getValue();

            int imageId = entry.getKey();
            println(getClass(), "ID: " + imageId, false, PRINT_DEBUG);

            String fileName = (String) itemNode.get("filename");
            println(getClass(), "FileName: " + fileName, false, PRINT_DEBUG);

            TileType tileType = TileType.valueOf((String) itemNode.get("tiletype"));
            println(getClass(), "TileLayer: " + tileType, false, PRINT_DEBUG);

            TileImage tileImage = new TileImage(imageId, fileName, tileType);

            // Set properties based on tile type
            switch (tileType) {
                case CLIFF:
                    break;
                case CONTAINER:
                    break;
                case DOOR:
                    break;
                case STATIC_IMAGE:
                    break;
                case WALL:
                    break;
            }

            TileLayers tileLayers = TileLayers.valueOf((String) itemNode.get("tilelayer"));
            tileImage.setTileLayers(tileLayers);
            println(getClass(), "TileLayer: " + tileLayers, false, PRINT_DEBUG);


            println(PRINT_DEBUG);

            worldImageMap.put(imageId, tileImage);
        }

        println(getClass(), "====== END LOADING TILES ======", false, PRINT_DEBUG);

        if (worldImageMap.isEmpty()) println(getClass(), "TilePropertiesMap is empty!", true, true);
        return worldImageMap;
    }
}
