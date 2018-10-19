package com.valenguard.client.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

@SuppressWarnings("WeakerAccess")
public class GraphicsUtils {

    private GraphicsUtils() {
    }

    public static void clearScreen() {
        clearScreen(0, 0, 0, 1);
    }

    public static void clearScreen(float red, float green, float blue, float alpha) {
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
