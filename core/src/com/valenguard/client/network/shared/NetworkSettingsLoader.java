package com.valenguard.client.network.shared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.Map;

import static com.valenguard.client.util.Log.println;

public class NetworkSettingsLoader {

    private static final boolean PRINT_DEBUG = true;

    public NetworkSettings loadNetworkSettings() {
        Yaml yaml = new Yaml();

        FileHandle fileHandle = Gdx.files.internal("data" + File.separator + "network.yaml");

        Map<String, Object> root = yaml.load(fileHandle.read());
        String IP = (String) root.get("ip");
        int port = (Integer) root.get("port");

        println(getClass(), IP + ":" + port, false, PRINT_DEBUG);

        return new NetworkSettings(IP, port);
    }

}
