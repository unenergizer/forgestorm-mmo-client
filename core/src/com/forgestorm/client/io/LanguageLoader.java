package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class LanguageLoader {

    private static final boolean PRINT_DEBUG = false;

    public Map<Integer, String> loadLanguageText() {

        println(getClass(), "====== START LOADING LANGUAGE ======", false, PRINT_DEBUG);

        FileHandle fileHandle = Gdx.files.internal(FilePaths.LANG_ENG.getFilePath());
        Yaml yaml = new Yaml();
        Map<Integer, String> root = yaml.load(fileHandle.read());

        Map<Integer, String> languageTextMap = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> entry : root.entrySet()) {

            int languageTextId = entry.getKey();
            String languageText = entry.getValue();

            println(getClass(), "LanguageTextID: " + languageTextId, false, PRINT_DEBUG);
            println(getClass(), " -Value: " + languageText, false, PRINT_DEBUG);

            println(PRINT_DEBUG);

        }

        println(getClass(), "====== END LOADING LANGUAGE ======", false, PRINT_DEBUG);
        return languageTextMap;
    }
}
