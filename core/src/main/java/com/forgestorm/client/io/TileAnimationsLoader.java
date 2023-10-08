package com.forgestorm.client.io;

import static com.forgestorm.client.util.Log.println;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.game.world.maps.tile.TileAnimation;

import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class TileAnimationsLoader extends SynchronousAssetLoader<TileAnimationsLoader.TileAnimationsDataWrapper, TileAnimationsLoader.TileAnimationsParameter> {

    static class TileAnimationsParameter extends AssetLoaderParameters<TileAnimationsDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;

    TileAnimationsLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public TileAnimationsDataWrapper load(AssetManager assetManager, String fileName, FileHandle file, TileAnimationsParameter parameter) {
        TileAnimationsDataWrapper tileAnimationsDataWrapper = new TileAnimationsDataWrapper();
        tileAnimationsDataWrapper.setTileAnimationMap(new HashMap<>());

        println(getClass(), "====== START LOADING TILE ANIMATIONS ======", false, PRINT_DEBUG);

        Yaml yaml = new Yaml();

        Map<Integer, Object> root = yaml.load(file.read());

        // Loop through all animations
        for (Map.Entry<Integer, Object> entry : root.entrySet()) {
            int animationID = entry.getKey();

            // Get the playback type for this animation
            //noinspection unchecked
            Map<String, String> playbackType = (Map<String, String>) entry.getValue();
            TileAnimation.PlaybackType animationControls = TileAnimation.PlaybackType.valueOf(playbackType.get("playbackType"));
            println(getClass(), "PlaybackType: " + animationControls, false, PRINT_DEBUG);

            TileAnimation tileAnimation = new TileAnimation(animationID, animationControls);
            println(getClass(), "Animation ID: " + animationID, false, PRINT_DEBUG);

            // Now get each individual frame for this animation
            //noinspection unchecked
            Map<String, Map<Integer, Map<String, Integer>>> animationFrameDataRoot = (Map<String, Map<Integer, Map<String, Integer>>>) entry.getValue();
            Map<Integer, Map<String, Integer>> animationFrameData = animationFrameDataRoot.get("animationFrames");

            // Loop through a particular animations frames and get info on each frame
            for (Map.Entry<Integer, Map<String, Integer>> entry1 : animationFrameData.entrySet()) {
                int frameID = entry1.getKey();
                Map<String, Integer> animationData = entry1.getValue();

                println(getClass(), " = Frame: " + frameID, false, PRINT_DEBUG);

                int tileId = animationData.get("tileId");
                println(getClass(), "  -- TileId: " + tileId, false, PRINT_DEBUG);

                int duration = animationData.get("duration");
                println(getClass(), "  -- Duration: " + duration, false, PRINT_DEBUG);

                tileAnimation.addAnimationFrame(frameID, tileId, duration);
            }

            // Add the tile animation to the list of tiles
            tileAnimationsDataWrapper.getTileAnimationMap().put(animationID, tileAnimation);

            println(getClass(), "", false, PRINT_DEBUG);
        }

        println(getClass(), "====== END LOADING TILE ANIMATIONS ======", false, PRINT_DEBUG);

        if (tileAnimationsDataWrapper.getTileAnimationMap().isEmpty()) {
            println(getClass(), "TileAnimationMap is empty!", true, true);
        }
        return tileAnimationsDataWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TileAnimationsParameter parameter) {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public static class TileAnimationsDataWrapper {
        private Map<Integer, TileAnimation> tileAnimationMap = null;
    }
}
