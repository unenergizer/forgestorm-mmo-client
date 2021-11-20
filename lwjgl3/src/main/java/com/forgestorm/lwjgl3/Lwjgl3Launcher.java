package com.forgestorm.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {

    private static final String usernameArg = "username:";
    private static final String passwordArg = "password:";

    public static void main(String[] args) {

        boolean forceLocalHost = false;
        boolean ideRun = false;
        boolean playIntroMusic = true;
        boolean ignoreRevisionNumber = false;
        String username = null;
        String password = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("forceLocalHost")) forceLocalHost = true;
            if (arg.equalsIgnoreCase("ideRun")) ideRun = true;
            if (arg.equalsIgnoreCase("muteIntroMusic")) playIntroMusic = false;
            if (arg.equalsIgnoreCase("ignoreRevisionNumber")) ignoreRevisionNumber = true;
            if (arg.contains(usernameArg)) username = arg.replace(usernameArg, "");
            if (arg.contains(passwordArg)) password = arg.replace(passwordArg, "");
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("RetroMMO");
        config.useVsync(true);

        //// Limits FPS to the refresh rate of the currently active monitor.
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setWindowedMode(ClientConstants.SCREEN_RESOLUTION.getWidth(), ClientConstants.SCREEN_RESOLUTION.getHeight());
        config.setWindowIcon("icon-128.png", "icon-64.png", "icon-32.png", "icon-16.png");

        ClientMain clientMain = ClientMain.getInstance();
        clientMain.setIdeRun(ideRun);
        clientMain.setForceLocalHost(forceLocalHost);
        clientMain.setPlayIntroMusic(playIntroMusic);
        clientMain.setIgnoreRevisionNumber(ignoreRevisionNumber);

        if (username != null && !username.isEmpty()) {
            clientMain.getLoginCredentials().setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            clientMain.getLoginCredentials().setPassword(password);
        }

        new Lwjgl3Application(clientMain, config);
    }
}