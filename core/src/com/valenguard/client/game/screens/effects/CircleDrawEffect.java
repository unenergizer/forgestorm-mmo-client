package com.valenguard.client.game.screens.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.UserInterfaceType;

public class CircleDrawEffect extends ScreenEffect {

    private final ShapeRenderer.ShapeType shapeType;
    private final Color color;
    private final float startX;
    private final float startY;
    private final float height;
    private final float maxLength;
    private final float speed;

    private ShapeRenderer shapeRenderer;
    private float num;

    public CircleDrawEffect(ShapeRenderer.ShapeType shapeType, Color color, float startX, float startY, float height, float maxLength, float speed) {
        this.shapeType = shapeType;
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        this.height = height;
        this.maxLength = maxLength;
        this.speed = speed;
    }

    @Override
    public void performEffect(float deltaTime) {
        if (Valenguard.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) return;
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
        shapeRenderer.begin(shapeType);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(startX, startY, num);
//        shapeRenderer.ellipse(startX, startY, num, num);
        shapeRenderer.end();
    }

    @Override
    void finishEffect() {
        shapeRenderer.dispose();
    }

}
