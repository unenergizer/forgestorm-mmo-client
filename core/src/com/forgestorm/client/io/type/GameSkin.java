package com.forgestorm.client.io.type;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameSkin {

    DEFAULT("tixel/x1/tixel.json");

    private final String filePath;

    GameSkin(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/skin/" + filePath;
    }
}
