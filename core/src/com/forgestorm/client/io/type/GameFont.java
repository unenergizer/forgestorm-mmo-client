package com.forgestorm.client.io.type;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameFont {
    TEST_FONT("testfont.fnt"),
    PIXEL("pixel.fnt"),
    VISITOR("visitor.fnt"),
    PIXEL_UNICODE("pixel_unicode.fnt");

    private final String filePath;

    GameFont(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/font/" + filePath;
    }
}
