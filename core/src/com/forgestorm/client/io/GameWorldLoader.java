package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
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
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.GameWorld;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.ApplicationUtil.userOnMobile;
import static com.forgestorm.client.util.Log.println;

public class GameWorldLoader extends AsynchronousAssetLoader<GameWorldLoader.GameWorldDataWrapper, GameWorldLoader.GameWorldParameter> {

    static class GameWorldParameter extends AssetLoaderParameters<GameWorldDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private static final String EXTENSION_TYPE = ".json";
    private GameWorldDataWrapper gameWorldDataWrapper = null;

    GameWorldLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, GameWorldParameter parameter) {
        println(getClass(), "Is Directory: " + file.isDirectory(), false, PRINT_DEBUG);
        println(getClass(), "Directory List Size: " + file.list().length, false, PRINT_DEBUG);
        println(getClass(), "Directory Name: " + file.name(), false, PRINT_DEBUG);

        gameWorldDataWrapper = null;
        gameWorldDataWrapper = new GameWorldDataWrapper();
        gameWorldDataWrapper.setGameWorlds(new HashMap<String, GameWorld>());
        if (userOnMobile() || ClientMain.getInstance().isIdeRun()) {
            loadMobile(file);
        } else {
            loadDesktopJar(file);
        }
    }

    @Override
    public GameWorldDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, GameWorldParameter parameter) {
        return gameWorldDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, GameWorldParameter parameter) {
        return null;
    }

    private void loadDesktopJar(FileHandle file) {
        Collection<String> files = ResourceList.getDirectoryResources(file.name(), EXTENSION_TYPE);

        for (String fileName : files) {
            String worldName = fileName.substring(FilePaths.MAPS.getFilePath().length() + 1);
            FileHandle fileHandle = Gdx.files.internal(file.name() + "/" + worldName);
            gameWorldDataWrapper.getGameWorlds().put(worldName.replace(EXTENSION_TYPE, ""), load(fileHandle));
        }
    }

    private void loadMobile(FileHandle file) {
        for (FileHandle entry : file.list()) {
            // make sure were only adding game map files
            if (entry.path().endsWith(EXTENSION_TYPE)) {
                gameWorldDataWrapper.getGameWorlds().put(entry.name().replace(EXTENSION_TYPE, ""), load(entry));
            }
        }
    }

    private GameWorld load(FileHandle fileHandle) {

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
    public class GameWorldDataWrapper {
        private Map<String, GameWorld> gameWorlds = null;
    }
}
