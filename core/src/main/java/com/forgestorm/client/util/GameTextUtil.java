package com.forgestorm.client.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.forgestorm.client.ClientMain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class GameTextUtil {

    private static final GlyphLayout glyphLayout = new GlyphLayout();

    public static void drawMessage(ClientMain clientMain, String message, Color color, float fontScale, float x, float y) {
        drawMessage(message, color, clientMain.getGameScreen().getFont(), fontScale, clientMain.getGameScreen().getSpriteBatch(), x, y);
    }

    public static void drawMessage(String message, Color color, BitmapFont font, float fontScale, SpriteBatch spriteBatch, float x, float y) {
//        // Draw shadow message
//        font.getData().setScale(fontScale);
//        font.setColor(Color.BLACK);
//        glyphLayout.setText(font, message);
//        font.draw(spriteBatch, message, x - (glyphLayout.width / 2) + .3f, y - .3f);

        // Draw colored message
        font.getData().setScale(fontScale);
        font.setColor(color);
        glyphLayout.setText(font, message);
        font.draw(spriteBatch, glyphLayout, x - (glyphLayout.width / 2), y);
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }
}
