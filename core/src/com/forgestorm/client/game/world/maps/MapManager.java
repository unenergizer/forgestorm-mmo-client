package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.io.FilePaths;
import com.forgestorm.client.io.JsonMapParser;
import com.forgestorm.client.io.ResourceList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

import static com.forgestorm.client.util.ApplicationUtil.userOnMobile;

/**
 * Map manager does not load maps for the GameScreen to use. It currently
 * holds information about the game map data such as tiles, warps, collision, etc.
 */
public class MapManager implements Disposable {

    private static final String EXTENSION_TYPE = ".json";

    private final Map<String, GameMap> gameMaps = new HashMap<String, GameMap>();
    @Getter
    private Color backgroundColor = Color.BLACK;

    public MapManager(boolean ideRun) {
        if (userOnMobile() || ideRun) {
            loadMobile();
        } else {
            loadDesktopJar();
        }
    }

    private void loadDesktopJar() {
        Collection<String> files = ResourceList.getDirectoryResources(FilePaths.MAPS.getFilePath(), EXTENSION_TYPE);

        for (String fileName : files) {
            String mapName = fileName.substring(FilePaths.MAPS.getFilePath().length() + 1);
            FileHandle fileHandle = Gdx.files.internal(FilePaths.MAPS.getFilePath() + "/" + mapName);
            gameMaps.put(mapName.replace(EXTENSION_TYPE, ""), JsonMapParser.load(fileHandle));
        }
    }

    private void loadMobile() {
        FileHandle fileHandle = Gdx.files.internal(FilePaths.MAPS.getFilePath());
        for (FileHandle entry : fileHandle.list()) {
            // make sure were only adding game map files
            if (entry.path().endsWith(EXTENSION_TYPE)) {
                gameMaps.put(entry.name().replace(EXTENSION_TYPE, ""), JsonMapParser.load(entry));
            }
        }
    }

    /**
     * Gets the game map associated with a map name. The map name is determined by
     * the file name of the game map file.
     *
     * @param mapName The name of the game map.
     * @return GameMap that contains information about this map.
     * @throws RuntimeException Requested map could not be found or was not loaded.
     */
    public GameMap getGameMap(String mapName) throws RuntimeException {

        GameMap gameMap;
        if (gameMaps.containsKey(mapName)) {
            gameMap = gameMaps.get(mapName);
        } else if (gameMaps.containsKey(mapName.replace(EXTENSION_TYPE, ""))) {
            gameMap = gameMaps.get(mapName.replace(EXTENSION_TYPE, ""));
        } else {
            throw new RuntimeException("Tried to get the map " + mapName + ", but it doesn't exist or was not loaded.");
        }

        // Set clear screen background color
        if (gameMap.getBackgroundColor() != null) {
            backgroundColor = gameMap.getBackgroundColor();
        } else {
            backgroundColor = Color.BLACK;
        }

        return gameMap;
    }

    @Override
    public void dispose() {
        gameMaps.clear();
    }
}
