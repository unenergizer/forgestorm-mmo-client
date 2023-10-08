package com.forgestorm.client.io.type;

@SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
public enum GameFont {
    BITCELL("bitcell_memesbruh03.ttf"),
    PIXEL("pixel.fnt"),
    PIXEL_UNICODE("pixel_unicode.fnt"),
    TEST_FONT("testfont.fnt"),
    VISITOR("visitor.fnt");

    private final String filePath;

    GameFont(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/font/" + filePath;
    }
}
