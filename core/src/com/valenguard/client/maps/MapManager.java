package com.valenguard.client.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.maps.data.TmxMap;
import com.valenguard.client.maps.file.TmxFileParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Map manager does not load maps for the GameScreen to use. It currently
 * holds information about the tmx map data such as tiles, warps, collision, etc.
 */
public class MapManager implements Disposable {

    private final Map<String, TmxMap> tmxMaps = new HashMap<String, TmxMap>();

    public MapManager() {
        loadAllMaps();
    }

    /**
     * This will dynamically load all TMX maps for the game.
     *
     * @throws RuntimeException No maps were found.
     */
    private void loadAllMaps() {
        FileHandle fileHandle = Gdx.files.internal(ClientConstants.MAP_DIRECTORY);
        for (FileHandle entry : fileHandle.list()) {
            // make sure were only adding tmx files
            if (entry.path().endsWith(".tmx"))
                tmxMaps.put(entry.name().replace(".tmx", ""), TmxFileParser.loadXMLFile(entry));
        }
    }

    /**
     * Gets the tmx map associated with a map name. The map name is determined by
     * the file name of the TMX map file.
     *
     * @param mapName The name of the TMX map.
     * @return TmxMap that contains information about this map.
     * @throws RuntimeException Requested map could not be found or was not loaded.
     */
    public TmxMap getTmxMap(String mapName) throws RuntimeException {
        if (tmxMaps.containsKey(mapName)) {
            return tmxMaps.get(mapName);
        } else if (tmxMaps.containsKey(mapName.replace(".tmx", ""))) {
            return tmxMaps.get(mapName.replace(".tmx", ""));
        } else {
            throw new RuntimeException("Tried to get the map " + mapName + ", but it doesn't exist or was not loaded.");
        }
    }

    @Override
    public void dispose() {
        tmxMaps.clear();
    }
}
