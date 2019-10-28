package com.valenguard.client.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;

import static com.valenguard.client.util.Log.println;

public class DesktopLauncher {

    private static final String unArg = "username:";
    private static final String pwArg = "password:";

    public static void main(String[] args) {

        boolean ideRun = false;
        String username = null;
        String password = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("ideRun")) ideRun = true;
            if (args[i].contains(unArg)) username = args[i].replace(unArg, "");
            if (args[i].contains(pwArg)) password = args[i].replace(pwArg, "");
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
            println(DesktopLauncher.class, "Username: " + username);
            valenguard.getLoginCredentials().setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            println(DesktopLauncher.class, "Password: " + password);
            valenguard.getLoginCredentials().setPassword(password);
        }

        new LwjglApplication(valenguard, config);
    }
}
