package com.forgestorm.client.io.type;

@SuppressWarnings({"SameParameterValue", "unused"})
public enum GamePixmap {

    CURSOR_1("cursor1.png");

    private final String filePath;

    GamePixmap(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "graphics/pixmap/" + filePath;
    }
}
