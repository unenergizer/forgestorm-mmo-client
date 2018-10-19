package com.valenguard.client.assets;

@SuppressWarnings("unused")
public enum GameMap {
    MAIN_TOWN("maintown"),
    SOUTH("south"),
    NORTH("north");

    private String filePath;

    GameMap(String filePath) {
        this.filePath = filePath;
    }

    public String getMapName() {
        return filePath + ".tmx";
    }

    public String getFilePath() {
        return "maps/" + getMapName();
    }

    /**
     * Returns the map associated with the map name.
     *
     * @param mapName The map name. Make sure to include .tmx in the map name
     * @return The map
     */
    public static GameMap getMapByName(String mapName) {
        for (GameMap gameMap : GameMap.values()) {
            if (mapName.replace(".tmx", "").equals(gameMap.filePath)) return gameMap;
        }
        throw new RuntimeException("Failed to get filepath for map by name: " + mapName);
    }
}
