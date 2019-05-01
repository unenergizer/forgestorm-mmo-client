package com.valenguard.client.io.type;

@SuppressWarnings({"SameParameterValue", "unused"})
public enum GameSound {
    EAT("17661_SFX_HumanEatingPotatoChips1.wav");

    private final String filePath;

    GameSound(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return "audio/sounds/" + filePath;
    }
}