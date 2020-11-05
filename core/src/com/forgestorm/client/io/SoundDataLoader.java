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

public class SoundDataLoader extends AsynchronousAssetLoader<SoundDataLoader.SoundDataWrapper, SoundDataLoader.SoundParameter> {

    static class SoundParameter extends AssetLoaderParameters<SoundDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private SoundDataWrapper soundDataWrapper = null;

    SoundDataLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SoundParameter parameter) {
        soundDataWrapper = null;
        soundDataWrapper = new SoundDataWrapper();

        println(getClass(), "====== START LOADING SOUND FX ======", false, PRINT_DEBUG);

        soundDataWrapper.setSoundDataMap(new HashMap<Short, AudioData>());

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
            audioData.setAudioType(AudioType.SOUND_FX);

            soundDataWrapper.getSoundDataMap().put((short) soundId, audioData);

            println(getClass(), "AudioID: " + soundId, false, PRINT_DEBUG);
            println(getClass(), " -FileName: " + name, false, PRINT_DEBUG);
            println(getClass(), " -Description: " + description, false, PRINT_DEBUG);

            println(PRINT_DEBUG);

        }

        println(getClass(), "====== END LOADING SOUND FX ======", false, PRINT_DEBUG);
    }

    @Override
    public SoundDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, SoundParameter parameter) {
        return soundDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SoundParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public class SoundDataWrapper {
        private Map<Short, AudioData> soundDataMap = null;
    }
}
