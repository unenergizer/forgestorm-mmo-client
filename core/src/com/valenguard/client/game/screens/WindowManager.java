package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.valenguard.client.game.screens.ui.actors.WindowModes;

import lombok.Getter;

@Getter
public class WindowManager {

    private boolean useVSync = false;
    private WindowModes currentWindowMode = WindowModes.WINDOW;
    private ScreenResolutions currentWindowResolution = ScreenResolutions.DESKTOP_1024_600;

    public void setUseVSync(boolean useVSync) {
        this.useVSync = useVSync;
        Gdx.graphics.setVSync(useVSync);
    }

    public void setWindowMode(WindowModes windowMode, ScreenResolutions screenResolutions) {
        currentWindowResolution = screenResolutions;
        setWindowMode(windowMode);
    }

    public void setWindowMode(WindowModes windowMode) {
        Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
        currentWindowMode = windowMode;

        switch (windowMode) {
            case FULL_SCREEN_NO_WINDOW:
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
                Gdx.graphics.setFullscreenMode(mode);
                break;
            case FULL_SCREEN_WINDOWED:
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
                break;
            case WINDOW:
            default:
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                Gdx.graphics.setWindowedMode(currentWindowResolution.getWidth(), currentWindowResolution.getHeight());
                break;
        }
    }
}
