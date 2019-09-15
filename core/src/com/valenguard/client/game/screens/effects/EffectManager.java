package com.valenguard.client.game.screens.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.valenguard.client.util.Log.println;

public class EffectManager {

    private static final boolean PRINT_DEBUG = false;

    private List<ScreenEffect> screenEffectList = new ArrayList<ScreenEffect>();

    public void addScreenEffect(ScreenEffect screenEffect) {
        screenEffectList.add(screenEffect);
    }

    public void tickScreenEffect(float delta) {
        // Do effect
        Iterator<ScreenEffect> iterator = screenEffectList.iterator();
        while (iterator.hasNext()) {
            ScreenEffect screenEffect = iterator.next();

            if (!isReady(screenEffect)) return;

            screenEffect.performEffect(delta);

            // Finish effect
            if (screenEffect.isComplete) {
                screenEffect.finishEffect();
                screenEffect.isStarted = false;
                screenEffect.isComplete = false;
                iterator.remove();
                println(getClass(), "ListSize: " + screenEffectList.size(), false, PRINT_DEBUG);
            }
        }
    }

    public void drawScreenEffect() {
        for (ScreenEffect screenEffect : screenEffectList) {
            if (isReady(screenEffect)) screenEffect.drawEffect();
        }
    }

    private boolean isReady(ScreenEffect screenEffect) {
        return screenEffect != null;
    }
}
