package com.forgestorm.client.io;

import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.forgestorm.client.util.Log.println;

public class ScriptLoader {

    private static final boolean PRINT_DEBUG = false;

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
