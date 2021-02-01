package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.forgestorm.client.game.world.maps.GameWorld;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class GameWorldLoader extends AsynchronousAssetLoader<GameWorldLoader.GameWorldDataWrapper, GameWorldLoader.GameWorldParameter> {

    static class GameWorldParameter extends AssetLoaderParameters<GameWorldDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = true;
    private static final String EXTENSION_TYPE = ".json";
    private GameWorldDataWrapper gameWorldDataWrapper = null;

    GameWorldLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, GameWorldParameter parameter) {
        gameWorldDataWrapper = null;
        gameWorldDataWrapper = new GameWorldDataWrapper();
        gameWorldDataWrapper.setGameWorlds(new HashMap<String, GameWorld>());

        JsonValue root = new JsonReader().parse(file.reader());

        String worlds = root.get("worlds").asString();
        String[] worldList = worlds.split(",");

        for (String worldName : worldList) {
            FileHandle worldFileHandle = new FileHandle(FilePaths.MAP_DIRECTORY.getFilePath() + worldName);
            gameWorldDataWrapper.getGameWorlds().put(worldName.replace(EXTENSION_TYPE, ""), load(worldFileHandle));
        }
    }

    @Override
    public GameWorldDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, GameWorldParameter parameter) {
        return gameWorldDataWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, GameWorldParameter parameter) {
        return null;
    }

    private GameWorld load(FileHandle fileHandle) {
        println(getClass(), "Loading world: " + fileHandle, false, PRINT_DEBUG);
        JsonValue root = new JsonReader().parse(fileHandle.reader());

        String worldName = fileHandle.name().replace(".json", "");
        int red = root.get("backgroundRed").asInt();
        int green = root.get("backgroundGreen").asInt();
        int blue = root.get("backgroundBlue").asInt();
        int alpha = root.get("backgroundAlpha").asInt();
        int widthInChunks = root.get("widthInChunks").asInt();
        int heightInChunks = root.get("heightInChunks").asInt();

        return new GameWorld(
                worldName,
                widthInChunks,
                heightInChunks,
                new Color(red / 255f, green / 255f, blue / 255f, alpha));
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public static class GameWorldDataWrapper {
        private Map<String, GameWorld> gameWorlds = null;
    }
}
