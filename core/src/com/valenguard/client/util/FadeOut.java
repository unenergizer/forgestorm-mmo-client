package com.valenguard.client.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import lombok.Getter;

public class FadeOut {

    private float fadePerTick = 0.01f;
    private float alpha = 1.0f;

    @Getter
    private boolean isFading = false;

    public void draw(SpriteBatch spriteBatch, Texture texture, float x, float y) {
        if (isFading) {
            alpha -= fadePerTick;
            if (alpha < 0.0f) alpha = 0.0f;
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, alpha);
            spriteBatch.draw(texture, x, y);
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            spriteBatch.draw(texture, x, y);
        }
    }

    public void startFade(int numberOfTicksToFade) {
        isFading = true;
        alpha = 1.0f;
        fadePerTick = 1.0f / (float) numberOfTicksToFade;
    }

    public void cancelFade() {
        isFading = false;
    }
}
