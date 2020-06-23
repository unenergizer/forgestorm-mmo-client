package com.forgestorm.client.game.language;

import com.forgestorm.client.io.LanguageLoader;

import java.util.Map;

public class LanguageManager {
    private static final boolean PRINT_DEBUG = false;

    private final Map<Integer, String> languageTextMap;

    public LanguageManager() {
        LanguageLoader languageLoader = new LanguageLoader();
        this.languageTextMap = languageLoader.loadLanguageText();
    }

    public String getString(int languageTextId) {
        if (languageTextMap.containsKey(languageTextId)) {
            return languageTextMap.get(languageTextId);
        } else {
            throw new IllegalArgumentException("Language text not found for ID " + languageTextId);
        }
    }
}
