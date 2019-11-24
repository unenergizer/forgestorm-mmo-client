package com.valenguard.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.valenguard.client.game.audio.AudioData;
import com.valenguard.client.game.audio.AudioType;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.valenguard.client.util.Log.println;

public class ScriptLoader {

    private static final boolean PRINT_DEBUG = true;

    public String loadScript(FileHandle fileHandle) {

        println(getClass(), "====== START LOADING NPC SCRIPTS ======", false, PRINT_DEBUG);

        BufferedReader reader = new BufferedReader(new InputStreamReader(fileHandle.read()));
        StringBuilder stringBuilder = new StringBuilder();
        try {

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        println(getClass(), "====== END LOADING NPC SCRIPTS ======", false, PRINT_DEBUG);
        return stringBuilder.toString();

    }
}
