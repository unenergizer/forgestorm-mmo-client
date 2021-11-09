package com.forgestorm.shared.io;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class NetworkSettingsLoader extends AsynchronousAssetLoader<NetworkSettingsLoader.NetworkSettingsData, NetworkSettingsLoader.NetworkSettingsParameter> {

    static class NetworkSettingsParameter extends AssetLoaderParameters<NetworkSettingsData> {
    }

    private static final boolean PRINT_DEBUG = false;
    private NetworkSettingsData networkSettingsData = null;

    public NetworkSettingsLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, NetworkSettingsParameter parameter) {
        networkSettingsData = null;
        Yaml yaml = new Yaml();

        Map<String, Map<String, Object>> root = yaml.load(file.read());

        int loginPort = (Integer) root.get("login").get("port");
        String loginIp = (String) root.get("login").get("ip");
        int gamePort = (Integer) root.get("game").get("port");
        String gameIp = (String) root.get("game").get("ip");

        println(getClass(), "LoginSettings: " + loginIp + ":" + loginPort, false, PRINT_DEBUG);
        println(getClass(), "GameSettings: " + gameIp + ":" + gamePort, false, PRINT_DEBUG);


        networkSettingsData = new NetworkSettingsData(loginIp, loginPort, gameIp, gamePort);
    }

    @Override
    public NetworkSettingsData loadSync(AssetManager manager, String fileName, FileHandle file, NetworkSettingsParameter parameter) {
        return networkSettingsData;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, NetworkSettingsParameter parameter) {
        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class NetworkSettingsData {
        private final String loginIp;
        private final int loginPort;
        private final String gameIp;
        private final int gamePort;
    }
}
