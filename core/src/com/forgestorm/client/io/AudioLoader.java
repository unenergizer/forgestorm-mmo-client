package com.forgestorm.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.forgestorm.client.game.audio.AudioData;
import com.forgestorm.client.game.audio.AudioType;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class AudioLoader {

    private static final boolean PRINT_DEBUG = false;

    public Map<Short, AudioData> loadSoundFX() {

        println(getClass(), "====== START LOADING SOUND FX ======", false, PRINT_DEBUG);

        FileHandle fileHandle = Gdx.files.internal(FilePaths.SOUND_FX.getFilePath());
        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(fileHandle.read());

        Map<Short, AudioData> soundFxMap = new HashMap<Short, AudioData>();
        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            Map<String, Object> itemNode = entry.getValue();

            int soundId = entry.getKey();
            String fileName = (String) itemNode.get("fileName");
            String description = (String) itemNode.get("description");

            AudioData audioData = new AudioData();
            audioData.setAudioId(soundId);
            audioData.setFileName(fileName);
            audioData.setDescription(description);
            audioData.setAudioType(AudioType.SOUND_FX);

            soundFxMap.put((short) soundId, audioData);

            println(getClass(), "AudioID: " + soundId, false, PRINT_DEBUG);
            println(getClass(), " -FileName: " + fileName, false, PRINT_DEBUG);
            println(getClass(), " -Description: " + description, false, PRINT_DEBUG);

            println(PRINT_DEBUG);

        }

        println(getClass(), "====== END LOADING SOUND FX ======", false, PRINT_DEBUG);
        return soundFxMap;
    }

    public Map<Short, AudioData> loadGameMusic() {

        println(getClass(), "====== START LOADING GAME MUSIC ======", false, PRINT_DEBUG);

        FileHandle fileHandle = Gdx.files.internal(FilePaths.GAME_MUSIC.getFilePath());
        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(fileHandle.read());

        Map<Short, AudioData> soundFxMap = new HashMap<Short, AudioData>();
        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            Map<String, Object> itemNode = entry.getValue();

            int soundId = entry.getKey();
            String fileName = (String) itemNode.get("fileName");
            String description = (String) itemNode.get("description");

            AudioData audioData = new AudioData();
            audioData.setAudioId(soundId);
            audioData.setFileName(fileName);
            audioData.setDescription(description);
            audioData.setAudioType(AudioType.GAME_MUSIC);

            soundFxMap.put((short) soundId, audioData);

            println(getClass(), "AudioID: " + soundId, false, PRINT_DEBUG);
            println(getClass(), " -FileName: " + fileName, false, PRINT_DEBUG);
            println(getClass(), " -Description: " + description, false, PRINT_DEBUG);

            println(PRINT_DEBUG);

        }

        println(getClass(), "====== END LOADING GAME MUSIC ======", false, PRINT_DEBUG);
        return soundFxMap;
    }
}
