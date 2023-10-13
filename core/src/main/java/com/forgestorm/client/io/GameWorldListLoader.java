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
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public class GameWorldListLoader extends AsynchronousAssetLoader<GameWorldListLoader.GameWorldListDataWrapper, GameWorldListLoader.GameWorldParameter> {

    static class GameWorldParameter extends AssetLoaderParameters<GameWorldListDataWrapper> {
    }

    private GameWorldListDataWrapper gameWorldListDataWrapper = null;

    GameWorldListLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, GameWorldParameter parameter) {
        gameWorldListDataWrapper = new GameWorldListDataWrapper();

        JsonValue root = new JsonReader().parse(file.reader());

        String worlds = root.get("worlds").asString();
        String[] worldList = worlds.split(",");

        gameWorldListDataWrapper.setGameWorlds(Arrays.asList(worldList));
    }

    @Override
    public GameWorldListDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, GameWorldParameter parameter) {
        return gameWorldListDataWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, GameWorldParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public static class GameWorldListDataWrapper {
        private List<String> gameWorlds = null;
    }
}
