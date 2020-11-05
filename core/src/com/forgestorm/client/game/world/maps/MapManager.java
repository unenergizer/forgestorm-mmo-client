package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;

import java.util.Map;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

/**
 * Map manager does not load maps for the GameScreen to use. It currently
 * holds information about the game map data such as tiles, warps, collision, etc.
 */
public class MapManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;

    private final Map<String, GameMap> gameMaps;

    @Getter
    private GameMap currentGameMap;

    public MapManager() {
        this.gameMaps = ClientMain.getInstance().getFileManager().getGameMapData().getGameMaps();
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
        if (gameMaps.containsKey(mapName)) return gameMaps.get(mapName);
        return null;
    }

    /**
     * Sets the tiled map to be rendered.
     *
     * @param mapName The tiled map based on name
     */
    public void setGameMap(String mapName) {

        println(getClass(), "Map Name: " + mapName, false, PRINT_DEBUG);
        currentGameMap = getGameMap(mapName);

        // Map loaded, now fade it in!
        ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);
    }

    @Override
    public void dispose() {
        gameMaps.clear();
    }
}
