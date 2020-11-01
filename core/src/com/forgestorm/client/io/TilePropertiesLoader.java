package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.forgestorm.client.game.screens.ui.actors.dev.world.BuildCategory;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.AbstractTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.BlockMoveDirectionProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.ContainerProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.DoorProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.InteractDamageProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.JumpToDirectionProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.LadderProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TileWalkOverSoundProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.WangTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.WaterProperty;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("unchecked")
public class TilePropertiesLoader {

    private static final boolean PRINT_DEBUG = false;

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

            // Create the TileImage
            TileImage tileImage = new TileImage(imageId, fileName, buildCategory);

            // Load properties based on tile type
            Map<String, Object> mapOfTileProperties = (Map<String, Object>) itemNode.get("tileProperties");

            if (mapOfTileProperties != null && !mapOfTileProperties.isEmpty()) {

                for (Map.Entry<String, Object> entrySet : mapOfTileProperties.entrySet()) {
                    TilePropertyTypes tilePropertyType = TilePropertyTypes.valueOf(entrySet.getKey());
                    Map<String, Object> abstractPropertyFieldsMap = (Map<String, Object>) entrySet.getValue();
                    AbstractTileProperty abstractTileProperty = null;

                    switch (tilePropertyType) {
                        case DOOR:
                            abstractTileProperty = new DoorProperty();
                            break;
                        case INTERACTIVE_CONTAINER:
                            abstractTileProperty = new ContainerProperty();
                            break;
                        case WANG_TILE:
                            abstractTileProperty = new WangTileProperty();
                            break;
                        case BLOCK_MOVE_DIRECTION:
                            abstractTileProperty = new BlockMoveDirectionProperty();
                            break;
                        case JUMP_TO_DIRECTION:
                            abstractTileProperty = new JumpToDirectionProperty();
                            break;
                        case LADDER:
                            abstractTileProperty = new LadderProperty();
                            break;
                        case WATER:
                            abstractTileProperty = new WaterProperty();
                            break;
                        case INTERACT_DAMAGE:
                            abstractTileProperty = new InteractDamageProperty();
                            break;
                        case WALK_OVER_SOUND:
                            abstractTileProperty = new TileWalkOverSoundProperty();
                            break;
                    }

                    // If we find the property, lets get it setup!
                    if (abstractTileProperty != null) {
                        abstractTileProperty.setTileImage(tileImage);
                        tileImage.setCustomTileProperty(abstractTileProperty.load(abstractPropertyFieldsMap, true));
                    } else {
                        println(getClass(), "WARNING: Tile property " + tilePropertyType.name() + " was NOT setup! Create a entry for it!", true);
                    }
                }
            } else {
                println(getClass(), "No properties detected for TileImage ID: " + tileImage.getImageId(), true);
            }

            // Get layer definition
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
}
