package com.valenguard.client.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.util.GraphicsUtils;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

@Getter
public class CharacterSelectScreen implements Screen {


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(Color.RED);
        println(getClass(), "sent to char select");
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
