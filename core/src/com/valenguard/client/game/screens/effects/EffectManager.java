package com.valenguard.client.game.screens.effects;

import lombok.Getter;
import lombok.Setter;

public class EffectManager {

    @Getter
    @Setter
    private ScreenEffect screenEffect;
    @Setter
    private boolean isActive = false;

    public void tickScreenEffect(float delta) {
        if (!isReady()) return;

        // Do effect
        screenEffect.performEffect(delta);

        // Finish effect
        if (screenEffect.isComplete) {
            screenEffect.finishEffect();
            isActive = false;
            screenEffect.isStarted = false;
            screenEffect.isComplete = false;
        }
    }

    public void drawScreenEffect() {
        if (isReady()) screenEffect.drawEffect();
    }

    private boolean isReady() {
        return screenEffect != null && isActive;
    }
}
