package com.forgestorm.client.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;


public class FactionLoader extends AsynchronousAssetLoader<FactionLoader.FactionDataWrapper, FactionLoader.FactionParameter> {

    static class FactionParameter extends AssetLoaderParameters<FactionDataWrapper> {
    }

    private static final boolean PRINT_DEBUG = false;
    private FactionDataWrapper factionDataWrapper = null;

    FactionLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, FactionParameter parameter) {
        factionDataWrapper = null;
        factionDataWrapper = new FactionDataWrapper();
        Yaml yaml = new Yaml();

        factionDataWrapper.setByteFactionDataMap(new HashMap<Byte, FactionData>());
        Map<Integer, Map<String, Object>> root = yaml.load(file.read());

        List<LoadFactionData> loadFactionData = new ArrayList<LoadFactionData>();

        for (Map.Entry<Integer, Map<String, Object>> factionInfo : root.entrySet()) {
            byte factionId = (byte) (int) factionInfo.getKey();
            Map<String, Object> factionData = factionInfo.getValue();
            String factionName = (String) factionData.get("name");
            @SuppressWarnings("unchecked")
            List<String> enemyFactions = (List<String>) factionData.get("enemyFactions");
            loadFactionData.add(new LoadFactionData(factionName, factionId, enemyFactions));
        }

        // Converting from faction names to faction ids.
        for (LoadFactionData loadedFactionData : loadFactionData) {
            byte[] enemyFactionIds = new byte[loadedFactionData.enemyFactions.size()];
            for (int i = 0; i < loadedFactionData.enemyFactions.size(); i++) {
                for (LoadFactionData otherFactionData : loadFactionData) {
                    if (otherFactionData.factionName.equals(loadedFactionData.enemyFactions.get(i))) {
                        enemyFactionIds[i] = otherFactionData.factionId;
                        break;
                    }
                }
            }
            factionDataWrapper.getByteFactionDataMap().put(loadedFactionData.factionId, new FactionData(loadedFactionData.factionName, enemyFactionIds));
        }

        // Print Map
        println(PRINT_DEBUG);
        println(getClass(), "=================== LOADING FACTIONS ===================", false, PRINT_DEBUG);
        for (Map.Entry<Byte, FactionData> entry : factionDataWrapper.getByteFactionDataMap().entrySet()) {
            println(getClass(), "ID: " + entry.getKey(), false, PRINT_DEBUG);
            println(getClass(), "Name: " + entry.getValue().getFactionName(), false, PRINT_DEBUG);

            for (Byte enemies : entry.getValue().getEnemyFactions()) {
                println(getClass(), "Enemy: [" + enemies + "] " + factionDataWrapper.getByteFactionDataMap().get(enemies).getFactionName(), false, PRINT_DEBUG);
            }
            println(PRINT_DEBUG);
        }
        println(getClass(), "=================== FINISHED LOADING FACTIONS ===================", false, PRINT_DEBUG);
        println(PRINT_DEBUG);
    }

    @Override
    public FactionDataWrapper loadSync(AssetManager manager, String fileName, FileHandle file, FactionParameter parameter) {
        return factionDataWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, FactionParameter parameter) {
        return null;
    }

    @AllArgsConstructor
    private class LoadFactionData {
        private String factionName;
        private byte factionId;
        private List<String> enemyFactions;
    }

    @Getter
    @AllArgsConstructor
    public class FactionData {
        private String factionName;
        private byte[] enemyFactions;
    }

    @SuppressWarnings("WeakerAccess")
    @Setter
    @Getter
    public class FactionDataWrapper {
        private Map<Byte, FactionData> byteFactionDataMap = null;
    }
}
