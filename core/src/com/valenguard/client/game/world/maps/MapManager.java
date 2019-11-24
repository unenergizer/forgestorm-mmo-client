package com.valenguard.client.game.world.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.io.FilePaths;
import com.valenguard.client.io.ResourceList;
import com.valenguard.client.io.TmxFileParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import static com.valenguard.client.util.ApplicationUtil.userOnMobile;

/**
 * Map manager does not load maps for the GameScreen to use. It currently
 * holds information about the tmx map data such as tiles, warps, collision, etc.
 */
public class MapManager implements Disposable {

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
        Collection<String> files = ResourceList.getDirectoryResources(FilePaths.MAPS.getFilePath(), ".tmx");

        for (String fileName : files) {
            String mapName = fileName.substring(FilePaths.MAPS.getFilePath().length() + 1);
            FileHandle fileHandle = Gdx.files.internal(FilePaths.MAPS.getFilePath() + "/" + mapName);
            gameMaps.put(mapName.replace(".tmx", ""), TmxFileParser.loadXMLFile(fileHandle));
        }
    }

    private void loadMobile() {
        FileHandle fileHandle = Gdx.files.internal(FilePaths.MAPS.getFilePath());
        for (FileHandle entry : fileHandle.list()) {
            // make sure were only adding tmx files
            if (entry.path().endsWith(".tmx")) {
                gameMaps.put(entry.name().replace(".tmx", ""), TmxFileParser.loadXMLFile(entry));
            }
        }
    }

    /**
     * Gets the tmx game map associated with a map name. The map name is determined by
     * the file name of the TMX map file.
     *
     * @param mapName The name of the TMX map.
     * @return GameMap that contains information about this map.
     * @throws RuntimeException Requested map could not be found or was not loaded.
     */
    public GameMap getGameMap(String mapName) throws RuntimeException {
        GameMap gameMap = null;
        if (gameMaps.containsKey(mapName)) {
            gameMap = gameMaps.get(mapName);
        } else if (gameMaps.containsKey(mapName.replace(".tmx", ""))) {
            gameMap = gameMaps.get(mapName.replace(".tmx", ""));
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
