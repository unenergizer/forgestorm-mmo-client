package com.valenguard.client.game.screens.effects;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.world.maps.MapRenderer;

import static com.valenguard.client.util.Log.println;

public class AlphaFlashEffect extends ScreenEffect {

    private MapRenderer mapRenderer;
    private float num;

    @Override
    public void performEffect(float deltaTime) {
        if (Valenguard.getInstance().getScreenType() != ScreenType.GAME) return;
        if (!isStarted) {
            isStarted = true;
            mapRenderer = Valenguard.gameScreen.getMapRenderer();
        }

        println(getClass(), "doing effect");

        if (num > 1) {
            num = 0f;
            isComplete = true;
            println(getClass(), "color changing fin");
        } else {

            mapRenderer.setMapColor(1, 1, 1, num);
            println(getClass(), "color changing: " + num);
            num = num + 0.02f;
        }

    }

    @Override
    void drawEffect() {
    }

    @Override
    void finishEffect() {
        mapRenderer.resetMapColor();
    }

}
