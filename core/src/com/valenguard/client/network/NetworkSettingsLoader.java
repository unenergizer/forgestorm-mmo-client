package com.valenguard.client.network;

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

        Map<String, Map<String, Object>> root = yaml.load(fileHandle.read());

        int loginPort = (Integer) root.get("login").get("port");
        String loginIp = (String) root.get("game").get("ip");
        int gamePort = (Integer) root.get("game").get("port");
        String gameIp = (String) root.get("game").get("ip");

        println(getClass(), "LoginSettings: " + loginIp + ":" + loginPort, false, PRINT_DEBUG);
        println(getClass(), "GameSettings: " + gameIp + ":" + gamePort, false, PRINT_DEBUG);

        return new NetworkSettings(loginIp, loginPort, gameIp, gamePort);
    }

}
