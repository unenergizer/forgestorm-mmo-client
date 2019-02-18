package com.valenguard.client.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.valenguard.client.Valenguard;

@SuppressWarnings("WeakerAccess")
public class GameTextUtil {

    private static final GlyphLayout glyphLayout = new GlyphLayout();

    public static void drawMessage(String message, Color color, float fontScale, float x, float y) {
        drawMessage(message, color, Valenguard.gameScreen.getFont(), fontScale, Valenguard.gameScreen.getSpriteBatch(), x, y);
    }

    public static void drawMessage(String message, Color color, BitmapFont font, float fontScale, SpriteBatch spriteBatch, float x, float y) {
        // Draw shadow message
        font.getData().setScale(fontScale);
        font.setColor(Color.BLACK);
        glyphLayout.setText(font, message);
        font.draw(spriteBatch, message, x - (glyphLayout.width / 2) + .3f, y - .3f);

        // Draw colored message
        font.getData().setScale(fontScale);
        font.setColor(color);
        glyphLayout.setText(font, message);
        font.draw(spriteBatch, glyphLayout, x - (glyphLayout.width / 2), y);
    }
}
