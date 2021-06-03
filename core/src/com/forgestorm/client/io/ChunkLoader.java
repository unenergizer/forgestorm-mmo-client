package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.game.world.maps.TileImage;
import com.forgestorm.client.game.world.maps.Warp;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class ChunkLoader extends AsynchronousAssetLoader<ChunkLoader.MapChunkDataWrapper, ChunkLoader.MapChunkParameter> {

    static class MapChunkParameter extends AssetLoaderParameters<MapChunkDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private static final String EXTENSION_TYPE = ".json";
    private MapChunkDataWrapper mapChunkDataWrapper = null;

    ChunkLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, MapChunkParameter parameter) {
        println(getClass(), "Is Directory: " + file.isDirectory(), false, PRINT_DEBUG);
        println(getClass(), "Directory List Size: " + file.list().length, false, PRINT_DEBUG);
        println(getClass(), "Directory Name: " + file.name(), false, PRINT_DEBUG);

        mapChunkDataWrapper = null;
        mapChunkDataWrapper = new MapChunkDataWrapper();

        mapChunkDataWrapper.setWorldChunk(load(file));
    }

    @Override
    public MapChunkDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, MapChunkParameter parameter) {
        return mapChunkDataWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MapChunkParameter parameter) {
        return null;
    }

    private WorldChunk load(FileHandle fileHandle) {

        JsonValue root = new JsonReader().parse(fileHandle.reader());

        String chunkName = fileHandle.name().replace(EXTENSION_TYPE, "");
        String[] parts = chunkName.split("\\.");
        short chunkX = Short.parseShort(parts[0]);
        short chunkY = Short.parseShort(parts[1]);

        //Map<LayerDefinition, TileImage[]> layers = new HashMap<LayerDefinition, TileImage[]>();
        WorldChunk chunk = new WorldChunk(chunkX, chunkY);

        for (LayerDefinition layerDefinition : LayerDefinition.values()) {
            TileImage[] layer = readLayer(layerDefinition.getLayerName(), root);

            // Individually add each TileImage to the chunk (NPE FIX)
            for (int i = 0; i < layer.length; i++) {
                chunk.setTileImage(layerDefinition, layer[i], i);
            }
        }

        JsonValue warpsArray = root.get("warps");
        if (warpsArray != null) {
            for (JsonValue jsonWarp = warpsArray.child; jsonWarp != null; jsonWarp = jsonWarp.next) {
                Warp warp = new Warp(
                        new Location(jsonWarp.get("toMap").asString(), jsonWarp.get("toX").asShort(), jsonWarp.get("toY").asShort()),
                        MoveDirection.valueOf(jsonWarp.get("facingDirection").asString())
                );
                chunk.addTileWarp(jsonWarp.get("x").asShort(), jsonWarp.get("y").asShort(), warp);
            }
        }

        return chunk;
    }

    @SuppressWarnings("SameParameterValue")
    private static TileImage[] readLayer(String layerName, JsonValue root) {

        if (root.has(layerName)) {
            String layer = root.get(layerName).asString();
            String[] imageIds = layer.split(",");
            Map<Integer, TileImage> tileImages = ClientMain.getInstance().getFileManager().getTilePropertiesData().getWorldImageMap();
            TileImage[] tiles = new TileImage[ClientConstants.CHUNK_SIZE * ClientConstants.CHUNK_SIZE];
            for (int y = 0; y < ClientConstants.CHUNK_SIZE; y++) {
                for (int x = 0; x < ClientConstants.CHUNK_SIZE; x++) {
                    TileImage tileImage = tileImages.get(Integer.parseInt(imageIds[x + y * ClientConstants.CHUNK_SIZE]));
                    tiles[x + y * ClientConstants.CHUNK_SIZE] = tileImage;
                }
            }
            return tiles;
        } else {
            return new TileImage[ClientConstants.CHUNK_SIZE * ClientConstants.CHUNK_SIZE];
        }
    }

    @Setter
    @Getter
    public static class MapChunkDataWrapper {
        private WorldChunk worldChunk;
    }
}
