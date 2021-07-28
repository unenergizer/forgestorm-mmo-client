package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.io.FileManager;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class WorldManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;

    private final Map<String, GameWorld> gameWorlds = new HashMap<String, GameWorld>();

    @Getter
    private GameWorld currentGameWorld;

    public WorldManager() {
        FileManager fileManager = ClientMain.getInstance().getFileManager();
        for (String worldName : fileManager.getGameWorldListData().getGameWorlds()) {
            println(getClass(), "Adding: " + worldName, false, PRINT_DEBUG);
            gameWorlds.put(worldName.replace(".json", ""), fileManager.getGameWorldData(worldName).getGameWorld());
        }
    }

    /**
     * Gets the game world associated with a world name. The world name is determined by
     * the file name of the game world file.
     *
     * @param worldName The name of the game world.
     * @return GameWorld that contains information about this world.
     * @throws RuntimeException Requested world could not be found or was not loaded.
     */
    public GameWorld getGameWorld(String worldName) throws RuntimeException {
        if (gameWorlds.containsKey(worldName)) {
            return gameWorlds.get(worldName);
        } else {
            new GameWorld(worldName, Color.BLUE);
        }
        return null;
    }

    /**
     * Sets the game world to be rendered.
     *
     * @param worldName The game world based on name
     */
    public void setGameWorld(String worldName) {
        // Do some clean up on the currently loaded world
        if (currentGameWorld != null) currentGameWorld.clearData();

        println(getClass(), "World Name: " + worldName, false, PRINT_DEBUG);
        currentGameWorld = getGameWorld(worldName);

        // Map loaded, now fade it in!
        ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getFadeWindow(), 0.2f);
    }

    @Override
    public void dispose() {
        gameWorlds.clear();
    }
}
