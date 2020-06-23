package com.forgestorm.client.game.screens.effects;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.world.maps.MapRenderer;

public class BlackFlashEffect extends ScreenEffect {

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

            mapRenderer.setMapColor(num, num, num, 1);
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
