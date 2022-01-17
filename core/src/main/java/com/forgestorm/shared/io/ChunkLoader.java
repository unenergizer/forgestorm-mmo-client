package com.forgestorm.shared.io;

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
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class ChunkLoader extends AsynchronousAssetLoader<ChunkLoader.WorldChunkDataWrapper, ChunkLoader.MapChunkParameter> {

    static class MapChunkParameter extends AssetLoaderParameters<WorldChunkDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private static final String EXTENSION_TYPE = ".json";

    private final String worldName;

    private WorldChunkDataWrapper worldChunkDataWrapper = null;

    public ChunkLoader(FileHandleResolver resolver, String worldName) {
        super(resolver);
        this.worldName = worldName;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, MapChunkParameter parameter) {
        println(getClass(), "File Location: " + file.path(), false, PRINT_DEBUG);
        println(getClass(), "Is Directory: " + file.isDirectory(), false, PRINT_DEBUG);
        println(getClass(), "Directory List Size: " + file.list().length, false, PRINT_DEBUG);
        println(getClass(), "Directory Name: " + file.name(), false, PRINT_DEBUG);
        println(PRINT_DEBUG);

        worldChunkDataWrapper = null;
        worldChunkDataWrapper = new WorldChunkDataWrapper();
        WorldChunk worldChunk = parseChunk(file);
        worldChunkDataWrapper.setWorldChunkFromDisk(worldChunk);
    }

    @Override
    public WorldChunkDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, MapChunkParameter parameter) {
        return worldChunkDataWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MapChunkParameter parameter) {
        return null;
    }

    private WorldChunk parseChunk(FileHandle fileHandle) {

        JsonValue root = new JsonReader().parse(fileHandle.reader());

        String chunkName = fileHandle.name().replace(EXTENSION_TYPE, "");
        String[] parts = chunkName.split("\\.");
        short chunkX = Short.parseShort(parts[0]);
        short chunkY = Short.parseShort(parts[1]);

        WorldChunk chunk = new WorldChunk(worldName, chunkX, chunkY);

        // Process Tile Layers
        for (Floors floor : Floors.values()) {
            for (LayerDefinition layerDefinition : LayerDefinition.values()) {
                readLayer(floor, layerDefinition, root, chunk);
            }
        }

        // TODO: MOVE WARP LOADING TO ITS OWN FILE AND LOADER!
        // Process Tile Warps
//        JsonValue warpsArray = root.get("warps");
//        if (warpsArray != null) {
//            for (JsonValue jsonWarp = warpsArray.child; jsonWarp != null; jsonWarp = jsonWarp.next) {
//                Warp warp = new Warp(
//                        new Location(jsonWarp.get("toMap").asString(), jsonWarp.get("toX").asInt(), jsonWarp.get("toY").asInt(), jsonWarp.get("toZ").asShort()),
//                        MoveDirection.valueOf(jsonWarp.get("facingDirection").asString())
//                );
//                chunk.addTileWarp(jsonWarp.get("x").asShort(), jsonWarp.get("y").asShort(), warp);
//            }
//        }

        return chunk;
    }

    private static void readLayer(Floors floor, LayerDefinition layerDefinition, JsonValue root, WorldChunk chunk) {

        WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
        JsonValue floorRoot = root.get(Short.toString(floor.getWorldZ()));
        String layer = floorRoot.get(layerDefinition.getLayerName()).asString();
        String[] imageIds = layer.split(",");
        for (int localY = 0; localY < ClientConstants.CHUNK_SIZE; localY++) {
            for (int localX = 0; localX < ClientConstants.CHUNK_SIZE; localX++) {

                println(ChunkLoader.class, "Processing Tile: " + layerDefinition + ", XYZ: " + localX + "/" + localY + "/" + floor.getWorldZ(), false, PRINT_DEBUG);
                // Get the TileImage
                int tileId = Integer.parseInt(imageIds[localX + localY * ClientConstants.CHUNK_SIZE]);
                TileImage tileImage = worldBuilder.getTileImage(tileId);

                // Set the TileImage to the Tile
                if (tileImage != null) {
                    println(ChunkLoader.class, " -- Setting TileImage", false, PRINT_DEBUG);
                    Tile tile = chunk.getTile(layerDefinition, localX, localY, floor);
                    tile.setTileImage(new TileImage(tileImage));
                }
            }
        }
    }

    @Setter
    @Getter
    public static class WorldChunkDataWrapper {
        private WorldChunk worldChunkFromDisk;
    }
}
