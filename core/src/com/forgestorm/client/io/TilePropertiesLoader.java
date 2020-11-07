package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.screens.ui.actors.dev.world.BuildCategory;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.AbstractTileProperty;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypeHelper;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.TilePropertyTypes;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("unchecked")
public class TilePropertiesLoader extends SynchronousAssetLoader<TilePropertiesLoader.TilePropertiesDataWrapper, TilePropertiesLoader.TilePropertiesParameter> {

    static class TilePropertiesParameter extends AssetLoaderParameters<TilePropertiesDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;

    TilePropertiesLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public TilePropertiesDataWrapper load(AssetManager assetManager, String fileName, FileHandle file, TilePropertiesParameter parameter) {
        TilePropertiesDataWrapper tilePropertiesDataWrapper = new TilePropertiesDataWrapper();

        println(getClass(), "====== START LOADING TILES ======", false, PRINT_DEBUG);

        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(file.read());

        tilePropertiesDataWrapper.setWorldImageMap(new HashMap<Integer, TileImage>());

        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            Map<String, Object> itemNode = entry.getValue();

            int imageId = entry.getKey();
            println(getClass(), "ID: " + imageId, false, PRINT_DEBUG);

            String name = (String) itemNode.get("fileName");
            println(getClass(), "FileName: " + name, false, PRINT_DEBUG);

            BuildCategory buildCategory = BuildCategory.valueOf((String) itemNode.get("buildCategory"));
            println(getClass(), "BuildCategory: " + buildCategory, false, PRINT_DEBUG);

            // Create the TileImage
            TileImage tileImage = new TileImage(imageId, name, buildCategory);

            // Load properties based on tile type
            Map<String, Object> mapOfTileProperties = (Map<String, Object>) itemNode.get("tileProperties");

            if (mapOfTileProperties != null && !mapOfTileProperties.isEmpty()) {

                for (Map.Entry<String, Object> entrySet : mapOfTileProperties.entrySet()) {
                    TilePropertyTypes tilePropertyType = TilePropertyTypes.valueOf(entrySet.getKey());
                    Map<String, Object> abstractPropertyFieldsMap = (Map<String, Object>) entrySet.getValue();
                    AbstractTileProperty abstractTileProperty = TilePropertyTypeHelper.getNewAbstractTileProperty(tilePropertyType);

                    // If we find the property, lets get it setup!
                    //noinspection ConstantConditions
                    if (abstractTileProperty != null) {
                        abstractTileProperty.setTileImage(tileImage);
                        tileImage.setCustomTileProperty(abstractTileProperty.load(abstractPropertyFieldsMap, PRINT_DEBUG));
                    } else {
                        println(getClass(), "WARNING: Tile property " + tilePropertyType.name() + " was NOT setup! Create a entry for it!", true);
                    }
                }
            }

            // Get layer definition
            String tileLayerValue = (String) itemNode.get("layerDefinition");
            if (tileLayerValue != null && !tileLayerValue.isEmpty()) {
                LayerDefinition tileLayers = LayerDefinition.valueOf(tileLayerValue);
                tileImage.setLayerDefinition(tileLayers);
                println(getClass(), "TileLayer: " + tileLayers, false, PRINT_DEBUG);
            }

            println(PRINT_DEBUG);

            tilePropertiesDataWrapper.getWorldImageMap().put(imageId, tileImage);
        }

        println(getClass(), "====== END LOADING TILES ======", false, PRINT_DEBUG);

        if (tilePropertiesDataWrapper.getWorldImageMap().isEmpty()) {
            println(getClass(), "TilePropertiesMap is empty!", true, true);
        }
        return tilePropertiesDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TilePropertiesParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public class TilePropertiesDataWrapper {
        private Map<Integer, TileImage> worldImageMap = null;
    }
}
