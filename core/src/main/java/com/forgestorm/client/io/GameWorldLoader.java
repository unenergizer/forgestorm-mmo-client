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
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.GameWorld;
import lombok.Getter;
import lombok.Setter;

public class GameWorldLoader extends AsynchronousAssetLoader<GameWorldLoader.GameWorldDataWrapper, GameWorldLoader.GameWorldParameter> {

    static class GameWorldParameter extends AssetLoaderParameters<GameWorldDataWrapper> {
    }

    private final ClientMain clientMain;
    private GameWorldDataWrapper gameWorldDataWrapper = null;

    GameWorldLoader(ClientMain clientMain, FileHandleResolver resolver) {
        super(resolver);
        this.clientMain = clientMain;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, GameWorldParameter parameter) {
        gameWorldDataWrapper = new GameWorldDataWrapper();

        JsonValue root = new JsonReader().parse(file.reader());

        String worldName = file.name().replace(".json", "");
        int red = root.get("backgroundRed").asInt();
        int green = root.get("backgroundGreen").asInt();
        int blue = root.get("backgroundBlue").asInt();
        int alpha = root.get("backgroundAlpha").asInt();

        GameWorld gameWorld = new GameWorld(clientMain,
                worldName,
                new Color(red / 255f, green / 255f, blue / 255f, alpha));

        gameWorldDataWrapper.setGameWorld(gameWorld);
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

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public static class GameWorldDataWrapper {
        private GameWorld gameWorld = null;
    }
}
