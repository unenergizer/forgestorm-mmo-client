package com.valenguard.client.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;

public class DesktopLauncher {
    public static void main(String[] args) {

        boolean ideRun = false;
        for (int i = 0; i < args.length; i++) {
            if (i == 0) if (args[0].equalsIgnoreCase("ideRun")) ideRun = true;
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = true;
        config.fullscreen = false;
        config.title = "Valenguard - A Retro MMO";
        config.width = ClientConstants.SCREEN_RESOLUTION.getWidth();
        config.height = ClientConstants.SCREEN_RESOLUTION.getHeight();

        config.addIcon("graphics/misc/icon-128.png", Files.FileType.Internal);
        config.addIcon("graphics/misc/icon-32.png", Files.FileType.Internal);
        config.addIcon("graphics/misc/icon-16.png", Files.FileType.Internal);

        Valenguard valenguard = Valenguard.getInstance();
        valenguard.setIdeRun(ideRun);

        if (ideRun) {
            valenguard.getLoginCredentials().setUsername("ForgeStorm");
            valenguard.getLoginCredentials().setPassword("abc123");
        }

        new LwjglApplication(valenguard, config);
    }
}
