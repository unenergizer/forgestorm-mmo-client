package com.valenguard.client.game.screens.effects;

import com.valenguard.client.ClientMain;
import com.valenguard.client.game.screens.UserInterfaceType;
import com.valenguard.client.game.world.maps.MapRenderer;

public class AlphaFlashEffect extends ScreenEffect {

    private MapRenderer mapRenderer;
    private float num;

    @Override
    public void performEffect(float deltaTime) {
        if (ClientMain.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) return;
        if (!isStarted) {
            isStarted = true;
            mapRenderer = ClientMain.gameScreen.getMapRenderer();
        }

        if (num > 1) {
            num = 0f;
            isComplete = true;
        } else {

            mapRenderer.setMapColor(1, 1, 1, num);
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
