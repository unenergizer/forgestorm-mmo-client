package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.audio.AudioData;
import com.forgestorm.client.game.audio.AudioType;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class MusicDataLoader extends AsynchronousAssetLoader<MusicDataLoader.MusicDataWrapper, MusicDataLoader.MusicParameter> {

    static class MusicParameter extends AssetLoaderParameters<MusicDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private MusicDataWrapper musicDataWrapper = null;

    MusicDataLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, MusicParameter parameter) {
        musicDataWrapper = null;
        musicDataWrapper = new MusicDataWrapper();
        println(getClass(), "====== START LOADING GAME MUSIC ======", false, PRINT_DEBUG);

        musicDataWrapper.setMusicDataMap(new HashMap<Short, AudioData>());

        Yaml yaml = new Yaml();
        Map<Integer, Map<String, Object>> root = yaml.load(file.read());

        for (Map.Entry<Integer, Map<String, Object>> entry : root.entrySet()) {
            Map<String, Object> itemNode = entry.getValue();

            int soundId = entry.getKey();
            String name = (String) itemNode.get("fileName");
            String description = (String) itemNode.get("description");

            AudioData audioData = new AudioData();
            audioData.setAudioId(soundId);
            audioData.setFileName(name);
            audioData.setDescription(description);
            audioData.setAudioType(AudioType.GAME_MUSIC);

            musicDataWrapper.getMusicDataMap().put((short) soundId, audioData);

            println(getClass(), "AudioID: " + soundId, false, PRINT_DEBUG);
            println(getClass(), " -FileName: " + name, false, PRINT_DEBUG);
            println(getClass(), " -Description: " + description, false, PRINT_DEBUG);

            println(PRINT_DEBUG);

        }

        println(getClass(), "====== END LOADING GAME MUSIC ======", false, PRINT_DEBUG);
    }

    @Override
    public MusicDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, MusicParameter parameter) {
        return musicDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MusicParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public class MusicDataWrapper {
        private Map<Short, AudioData> musicDataMap = null;
    }
}
