package com.valenguard.client.io.type;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameFont {
    TEST_FONT("testfont.fnt");

    private final String filePath;

    GameFont(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/font/" + filePath;
    }
}
