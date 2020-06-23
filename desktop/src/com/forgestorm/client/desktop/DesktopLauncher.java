package com.forgestorm.client.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;

public class DesktopLauncher {

    private static final String usernameArg = "username:";
    private static final String passwordArg = "password:";

    public static void main(String[] args) {

        boolean forceLocalHost = false;
        boolean ideRun = false;
        String username = null;
        String password = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("forceLocalHost")) forceLocalHost = true;
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
        config.x = 0;
        config.y = 0;

        config.addIcon("graphics/misc/icon-128.png", Files.FileType.Internal);
        config.addIcon("graphics/misc/icon-32.png", Files.FileType.Internal);
        config.addIcon("graphics/misc/icon-16.png", Files.FileType.Internal);

        ClientMain clientMain = ClientMain.getInstance();
        clientMain.setIdeRun(ideRun);
        clientMain.setForceLocalHost(forceLocalHost);

        if (username != null && !username.isEmpty()) {
            clientMain.getLoginCredentials().setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            clientMain.getLoginCredentials().setPassword(password);
        }

        new LwjglApplication(clientMain, config);
    }
}
