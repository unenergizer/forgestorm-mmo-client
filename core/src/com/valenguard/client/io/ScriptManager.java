package com.valenguard.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.valenguard.client.game.world.entities.Entity;

import org.yaml.snakeyaml.Yaml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import static com.valenguard.client.util.ApplicationUtil.userOnMobile;

@Getter
public class ScriptManager {

    private ScriptLoader scriptLoader;

    private static final Map<Integer, String> LOADED_SCRIPTS = new HashMap<Integer, String>();

    public ScriptManager(boolean ideRun) {
        scriptLoader = new ScriptLoader();
        Map<String, Integer> scriptNameMapping = loadScriptNameMapping();
        if (userOnMobile() || ideRun) {
            loadMobile(scriptNameMapping);
        } else {
            loadDesktop(scriptNameMapping);
        }
    }

    private Map<String, Integer> loadScriptNameMapping() {

        FileHandle fileHandle = Gdx.files.internal(FilePaths.SCRIPT_MAPPING.getFilePath());
        Yaml yaml = new Yaml();
        Map<Integer, String> root = yaml.load(fileHandle.read());

        Map<String, Integer> reverse = new HashMap<String, Integer>();
        for (Map.Entry<Integer, String> entry : root.entrySet()) {
            reverse.put(entry.getValue(), entry.getKey());
        }

        return reverse;
    }

    private void loadDesktop(Map<String, Integer> scriptNameMapping) {
        Collection<String> files = ResourceList.getDirectoryResources(FilePaths.SCRIPTS.getFilePath(), ".js");

        for (String fileName : files) {
            //System.out.println(fileName);
            //String mapName = fileName.substring(FilePaths.MAPS.getFilePath().length() + 1);
            //FileHandle fileHandle = Gdx.files.internal(FilePaths.MAPS.getFilePath() + "/" + mapName);
            // gameMaps.put(mapName.replace(".tmx", ""), TmxFileParser.loadXMLFile(fileHandle));
        }
    }

    private void loadMobile(Map<String, Integer> scriptNameMapping) {
        FileHandle fileHandle = Gdx.files.internal(FilePaths.SCRIPTS.getFilePath());
        for (FileHandle entry : fileHandle.list()) {
            if (entry.path().endsWith(".js")) {
                String scriptContent = scriptLoader.loadScript(entry);
                LOADED_SCRIPTS.put(scriptNameMapping.get(entry.name()), scriptContent);
            }
        }
    }

    public String getScript(int scriptId) {
        return LOADED_SCRIPTS.get(scriptId);
    }
}
