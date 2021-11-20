package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangTile;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.wang.WangType;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class WangPropertiesLoader extends SynchronousAssetLoader<WangPropertiesLoader.WangPropertiesDataWrapper, WangPropertiesLoader.WangPropertiesParameter> {

    static class WangPropertiesParameter extends AssetLoaderParameters<WangPropertiesDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;

    WangPropertiesLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public WangPropertiesDataWrapper load(AssetManager assetManager, String fileName, FileHandle file, WangPropertiesParameter parameter) {
        WangPropertiesDataWrapper wangPropertiesDataWrapper = new WangPropertiesDataWrapper();

        println(getClass(), "====== START LOADING WANG TILES ======", false, PRINT_DEBUG);

        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(file.read());

        wangPropertiesDataWrapper.setWangImageMap(new HashMap<Integer, WangTile>());

        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            Map<String, Object> itemNode = entry.getValue();

            int wangId = entry.getKey();
            println(getClass(), "ID: " + wangId, false, PRINT_DEBUG);

            String routeName = (String) itemNode.get("fileName");
            println(getClass(), "FileName: " + routeName, false, PRINT_DEBUG);

            String type = (String) itemNode.get("type");
            println(getClass(), "Type: " + type, false, PRINT_DEBUG);

            // Create the TileImage
            WangTile tileImage = new WangTile(wangId, routeName, WangType.valueOf(type));

            println(PRINT_DEBUG);

            wangPropertiesDataWrapper.getWangImageMap().put(wangId, tileImage);
        }

        println(getClass(), "====== END LOADING WANG TILES ======", false, PRINT_DEBUG);

        if (wangPropertiesDataWrapper.getWangImageMap().isEmpty()) {
            println(getClass(), "WangPropertiesMap is empty!", true, true);
        }
        return wangPropertiesDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, WangPropertiesParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public static class WangPropertiesDataWrapper {
        private Map<Integer, WangTile> wangImageMap = null;
    }
}
