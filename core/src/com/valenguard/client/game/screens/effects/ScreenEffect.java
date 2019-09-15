package com.valenguard.client.game.screens.effects;

abstract class ScreenEffect {

    boolean isStarted = false;
    boolean isComplete = false;

    abstract void performEffect(float deltaTime);

    abstract void drawEffect();

    abstract void finishEffect();
}
