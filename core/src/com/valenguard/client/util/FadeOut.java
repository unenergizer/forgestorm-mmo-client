package com.valenguard.client.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import lombok.Getter;

public class FadeOut {

    private float fadePerTick = 0.01f;
    private float alpha = 1.0f;

    @Getter
    private boolean isFading = false;

    public void draw(SpriteBatch spriteBatch, TextureRegionDrawable texture, float x, float y, int width, int height) {
        if (isFading) {
            alpha -= fadePerTick;
            if (alpha < 0.0f) alpha = 0.0f;
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, alpha);
            spriteBatch.draw(texture.getRegion(), x, y, (float) width, (float) height);
            spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            spriteBatch.draw(texture.getRegion(), x, y, (float) width, (float) height);
        }
    }

    public void draw(SpriteBatch spriteBatch, TextureRegionDrawable texture, float x, float y) {
        draw(spriteBatch, texture, x, y, texture.getRegion().getRegionWidth(), texture.getRegion().getRegionHeight());
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
