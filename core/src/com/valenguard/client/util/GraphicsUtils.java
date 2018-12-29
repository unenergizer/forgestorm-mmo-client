package com.valenguard.client.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GraphicsUtils {

    private GraphicsUtils() {
    }

    public static void clearScreen() {
        clearScreen(0, 0, 0, 1);
    }

    public static void clearScreen(float red, float green, float blue, float alpha) {
        // Here we divide by 255 inorder to get the correct color from programs like
        // Adobe PhotoShop or Microsoft Paint.
        Gdx.gl.glClearColor(red / 255f, green / 255f, blue / 255f, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
