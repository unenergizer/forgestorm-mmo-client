package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.world.maps.Region;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class RegionLoader extends SynchronousAssetLoader<RegionLoader.RegionDataWrapper, RegionLoader.RegionParameter> {

    static class RegionParameter extends AssetLoaderParameters<RegionDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;

    RegionLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public RegionDataWrapper load(AssetManager assetManager, String fileName, FileHandle file, RegionParameter parameter) {
        RegionDataWrapper regionDataWrapper = new RegionDataWrapper();

        println(getClass(), "====== START LOADING REGIONS ======", false, PRINT_DEBUG);

        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(file.read());

        regionDataWrapper.setRegionMap(new HashMap<Integer, Region>());

        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            Map<String, Object> itemNode = entry.getValue();

            int regionId = entry.getKey();
            println(getClass(), "ID: " + regionId, false, PRINT_DEBUG);

            String worldName = (String) itemNode.get("worldName");
            println(getClass(), "WorldName: " + worldName, false, PRINT_DEBUG);

            int x1 = (Integer) itemNode.get("x1");
            println(getClass(), "x1: " + x1, false, PRINT_DEBUG);
            int y1 = (Integer) itemNode.get("y1");
            println(getClass(), "y1: " + y1, false, PRINT_DEBUG);

            int x2 = (Integer) itemNode.get("x2");
            println(getClass(), "x2: " + x2, false, PRINT_DEBUG);
            int y2 = (Integer) itemNode.get("y2");
            println(getClass(), "y2: " + y2, false, PRINT_DEBUG);

            // Create the TileImage
            Region region = new Region(worldName, x1, y1, x2, y2);

            println(PRINT_DEBUG);

            regionDataWrapper.getRegionMap().put(regionId, region);
        }

        println(getClass(), "====== END LOADING REGIONS ======", false, PRINT_DEBUG);
        return regionDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, RegionParameter parameter) {
        return null;
    }

    @Setter
    @Getter
    public static class RegionDataWrapper {
        private Map<Integer, Region> regionMap = null;
    }
}
