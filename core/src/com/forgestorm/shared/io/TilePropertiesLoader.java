package com.forgestorm.shared.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.properties.AbstractTileProperty;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.shared.game.world.maps.Tags;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypeHelper;
import com.forgestorm.shared.game.world.tile.properties.TilePropertyTypes;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("unchecked")
public class TilePropertiesLoader extends SynchronousAssetLoader<TilePropertiesLoader.TilePropertiesDataWrapper, TilePropertiesLoader.TilePropertiesParameter> {

    static class TilePropertiesParameter extends AssetLoaderParameters<TilePropertiesDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;

    public TilePropertiesLoader(FileHandleResolver resolver) {
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

            LayerDefinition layerDefinition = LayerDefinition.valueOf((String) itemNode.get("layerDefinition"));
            println(getClass(), "LayerDefinition: " + layerDefinition, false, PRINT_DEBUG);

            // Create the TileImage
            TileImage tileImage = new TileImage(imageId, name, layerDefinition);

            // Load tile tags
            List<String> tagsList = (List<String>) itemNode.get("tagsList");
            if (tagsList != null && !tagsList.isEmpty()) {
                for (String tag : tagsList) {
                    Tags tagValue = Tags.valueOfEnum(tag);

                    // Only add the Tag if it still exists in code (Tags can be added/removed)
                    // Don't crash the client if a tag is removed. Silently ignore a bad tag.
                    if (tagValue != null && tagValue != Tags.AN_UNUSED_TAG) {
                        tileImage.addTag(tagValue);
                    }
                }
            }

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

            println(PRINT_DEBUG);

            tilePropertiesDataWrapper.getWorldImageMap().put(imageId, tileImage);
        }

        println(getClass(), "====== END LOADING TILES ======", false, PRINT_DEBUG);

        if (tilePropertiesDataWrapper.getWorldImageMap().isEmpty()) {
            println(getClass(), "TilePropertiesMap is empty!", true, true);
        }
        return tilePropertiesDataWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TilePropertiesParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public static class TilePropertiesDataWrapper {
        private Map<Integer, TileImage> worldImageMap = null;
    }
}
