package com.valenguard.client.game.screens.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ScreenType;

public class LineDrawEffect extends ScreenEffect {

    private final Color color;
    private final float startX;
    private final float startY;
    private final float height;
    private final float maxLength;
    private final float speed;

    private ShapeRenderer shapeRenderer;
    private float num;

    public LineDrawEffect(Color color, float startX, float startY, float height, float maxLength, float speed) {
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        this.height = height;
        this.maxLength = maxLength;
        this.speed = speed;
    }

    @Override
    public void performEffect(float deltaTime) {
        if (Valenguard.getInstance().getScreenType() != ScreenType.GAME) return;
        if (!isStarted) {
            isStarted = true;
            shapeRenderer = new ShapeRenderer();
        }

        if (num >= maxLength) {
            num = 0f;
            isComplete = true;
        } else {

            num = num + speed;
        }

    }

    @Override
    void drawEffect() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(startX, startY, num, height);
        shapeRenderer.end();
    }

    @Override
    void finishEffect() {
        shapeRenderer.dispose();
    }

}
