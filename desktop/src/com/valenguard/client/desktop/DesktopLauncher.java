package com.valenguard.client.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;

public class DesktopLauncher {

    private static final String usernameArg = "username:";
    private static final String passwordArg = "password:";

    public static void main(String[] args) {

        boolean ideRun = false;
        String username = null;
        String password = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("ideRun")) ideRun = true;
            if (arg.contains(usernameArg)) username = arg.replace(usernameArg, "");
            if (arg.contains(passwordArg)) password = arg.replace(passwordArg, "");
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = true;
        config.fullscreen = false;
        config.title = "RetroMMO";
        config.width = ClientConstants.SCREEN_RESOLUTION.getWidth();
        config.height = ClientConstants.SCREEN_RESOLUTION.getHeight();

        config.addIcon("graphics/misc/icon-128.png", Files.FileType.Internal);
        config.addIcon("graphics/misc/icon-32.png", Files.FileType.Internal);
        config.addIcon("graphics/misc/icon-16.png", Files.FileType.Internal);

        Valenguard valenguard = Valenguard.getInstance();
        valenguard.setIdeRun(ideRun);

        if (username != null && !username.isEmpty()) {
            valenguard.getLoginCredentials().setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            valenguard.getLoginCredentials().setPassword(password);
        }

        new LwjglApplication(valenguard, config);
    }
}
