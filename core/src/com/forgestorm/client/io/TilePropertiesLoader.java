package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.forgestorm.client.game.screens.ui.actors.dev.world.BuildCategory;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.DecorationType;
import com.forgestorm.client.game.screens.ui.actors.dev.world.properties.ContainerProperties;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

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

            String fileName = (String) itemNode.get("fileName");
            println(getClass(), "FileName: " + fileName, false, PRINT_DEBUG);

            BuildCategory buildCategory = BuildCategory.valueOf((String) itemNode.get("buildCategory"));
            println(getClass(), "BuildCategory: " + buildCategory, false, PRINT_DEBUG);

            // Load properties based on tile type
            Map<String, Object> tileProperties = (Map<String, Object>) itemNode.get("customTileProperties");

            TileImage tileImage = new TileImage(imageId, fileName, buildCategory);

            switch (buildCategory) {
                case DECORATION:
                    parseDecorations(tileProperties, tileImage);
                    break;
                case TERRAIN:
                    break;
                case WALL:
                    break;
                case ROOF:
                    break;
                case UNDEFINED:
                    break;
            }

            String tileLayerValue = (String) itemNode.get("layerDefinition");
            if (tileLayerValue != null && !tileLayerValue.isEmpty()) {
                LayerDefinition tileLayers = LayerDefinition.valueOf(tileLayerValue);
                tileImage.setLayerDefinition(tileLayers);
                println(getClass(), "TileLayer: " + tileLayers, false, PRINT_DEBUG);
            }

            println(PRINT_DEBUG);

            worldImageMap.put(imageId, tileImage);
        }

        println(getClass(), "====== END LOADING TILES ======", false, PRINT_DEBUG);

        if (worldImageMap.isEmpty()) println(getClass(), "TilePropertiesMap is empty!", true, true);
        return worldImageMap;
    }

    private void parseDecorations(Map<String, Object> tileProperties, TileImage tileImage) {

        // First get decoration type
        DecorationType decorationType = DecorationType.valueOf((String) tileProperties.get("decorationType"));
        println(getClass(), "DecorationType: " + decorationType, false, PRINT_DEBUG);

        // Now do loading based on decoration type
        if (tileProperties != null && !tileProperties.isEmpty()) {
            switch (decorationType) {
                case BED:
                    break;
                case CHAIR:
                    break;
                case CONTAINER:
                    ContainerProperties containerProperties = new ContainerProperties(decorationType);
                    tileImage.setCustomTileProperties(containerProperties.load(tileProperties, PRINT_DEBUG));
                    break;
                case TABLE:
                    break;
            }
        }
    }
}
