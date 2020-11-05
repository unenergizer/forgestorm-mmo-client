package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.world.maps.GameMap;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.game.world.maps.Warp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.ApplicationUtil.userOnMobile;
import static com.forgestorm.client.util.Log.println;

public class GameMapLoader extends SynchronousAssetLoader<GameMapLoader.GameMapDataWrapper, GameMapLoader.GameMapParameter> {

    static class GameMapParameter extends AssetLoaderParameters<GameMapDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private static final String EXTENSION_TYPE = ".json";
    private GameMapDataWrapper gameMapDataWrapper = null;

    GameMapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public GameMapDataWrapper load(AssetManager assetManager, String fileName, FileHandle file, GameMapParameter parameter) {
        println(getClass(), "Is Directory: " + file.isDirectory(), false, PRINT_DEBUG);
        println(getClass(), "Directory List Size: " + file.list().length, false, PRINT_DEBUG);
        println(getClass(), "Directory Name: " + file.name(), false, PRINT_DEBUG);

        gameMapDataWrapper = null;
        gameMapDataWrapper = new GameMapDataWrapper();
        gameMapDataWrapper.setGameMaps(new HashMap<String, GameMap>());
        if (userOnMobile() || ClientMain.getInstance().isIdeRun()) {
            loadMobile(file);
        } else {
            loadDesktopJar(file);
        }
        return gameMapDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, GameMapParameter parameter) {
        return null;
    }

    private void loadDesktopJar(FileHandle file) {
        Collection<String> files = ResourceList.getDirectoryResources(file.name(), EXTENSION_TYPE);

        for (String fileName : files) {
            String mapName = fileName.substring(FilePaths.MAPS.getFilePath().length() + 1);
            FileHandle fileHandle = Gdx.files.internal(file.name() + "/" + mapName);
            gameMapDataWrapper.getGameMaps().put(mapName.replace(EXTENSION_TYPE, ""), load(fileHandle));
        }
    }

    private void loadMobile(FileHandle file) {
        for (FileHandle entry : file.list()) {
            // make sure were only adding game map files
            if (entry.path().endsWith(EXTENSION_TYPE)) {
                gameMapDataWrapper.getGameMaps().put(entry.name().replace(EXTENSION_TYPE, ""), load(entry));
            }
        }
    }

    private GameMap load(FileHandle fileHandle) {

        JsonValue root = new JsonReader().parse(fileHandle.reader());

        String mapName = fileHandle.name().replace(".json", "");
        int red = root.get("mapBackgroundRed").asInt();
        int green = root.get("mapBackgroundGreen").asInt();
        int blue = root.get("mapBackgroundBlue").asInt();
        int alpha = root.get("mapBackgroundAlpha").asInt();
        int mapWidth = root.get("mapWidth").asInt();
        int mapHeight = root.get("mapHeight").asInt();

        Map<Integer, TileImage[]> layers = new HashMap<Integer, TileImage[]>();

        TileImage[] layer = readLayer("layer1", root, mapWidth, mapHeight);

        layers.put(0, layer);

        GameMap gameMap = new GameMap();
        gameMap.setMapName(mapName);
        gameMap.setMapWidth(mapWidth);
        gameMap.setMapHeight(mapHeight);
        gameMap.setLayers(layers);
        gameMap.setBackgroundColor(new Color(red / 255f, green / 255f, blue / 255f, alpha));

        JsonValue warpsArray = root.get("warps");
        for (JsonValue jsonWarp = warpsArray.child; jsonWarp != null; jsonWarp = jsonWarp.next) {
            Warp warp = new Warp(
                    new Location(jsonWarp.get("toMap").asString(), jsonWarp.get("toX").asShort(), jsonWarp.get("toY").asShort()),
                    MoveDirection.valueOf(jsonWarp.get("facingDirection").asString())
            );
            gameMap.addTileWarp(jsonWarp.get("x").asShort(), jsonWarp.get("y").asShort(), warp);
        }

        return gameMap;
    }

    @SuppressWarnings("SameParameterValue")
    private static TileImage[] readLayer(String layerName, JsonValue root, int mapWidth, int mapHeight) {
        String layer = root.get(layerName).asString();
        String[] imageIds = layer.split(",");
        Map<Integer, TileImage> tileImages = ClientMain.getInstance().getFileManager().getTilePropertiesData().getWorldImageMap();
        TileImage[] tiles = new TileImage[mapWidth * mapHeight];
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                TileImage tileImage = tileImages.get(Integer.parseInt(imageIds[x + y * mapWidth]));
                tiles[x + y * mapWidth] = tileImage;
            }
        }
        return tiles;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public class GameMapDataWrapper {
        private Map<String, GameMap> gameMaps = null;
    }
}
