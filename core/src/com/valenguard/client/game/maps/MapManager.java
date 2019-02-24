package com.valenguard.client.game.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.data.ResourceList;
import com.valenguard.client.game.data.TmxFileParser;
import com.valenguard.client.game.maps.data.GameMap;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.valenguard.client.util.ApplicationUtil.userOnMobile;

/**
 * Map manager does not load maps for the GameScreen to use. It currently
 * holds information about the tmx map data such as tiles, warps, collision, etc.
 */
public class MapManager implements Disposable {

    private final Map<String, GameMap> gameMaps = new HashMap<String, GameMap>();

    public MapManager(boolean ideRun) {
        if (userOnMobile() || ideRun) {
            loadMobile();
        } else {
            loadDesktopJar();
        }
    }

    private void loadDesktopJar() {
        Collection<String> files = ResourceList.getMapResources(ClientConstants.MAP_DIRECTORY, ".tmx");

        for (String fileName : files) {
            String mapName = fileName.substring(ClientConstants.MAP_DIRECTORY.length() + 1);
            FileHandle fileHandle = Gdx.files.internal(ClientConstants.MAP_DIRECTORY + File.separator + mapName);
            gameMaps.put(mapName.replace(".tmx", ""), TmxFileParser.loadXMLFile(fileHandle));
        }
    }

    private void loadMobile() {
        FileHandle fileHandle = Gdx.files.internal(ClientConstants.MAP_DIRECTORY);
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
        if (gameMaps.containsKey(mapName)) {
            return gameMaps.get(mapName);
        } else if (gameMaps.containsKey(mapName.replace(".tmx", ""))) {
            return gameMaps.get(mapName.replace(".tmx", ""));
        } else {
            throw new RuntimeException("Tried to get the map " + mapName + ", but it doesn't exist or was not loaded.");
        }
    }

    @Override
    public void dispose() {
        gameMaps.clear();
    }
}
