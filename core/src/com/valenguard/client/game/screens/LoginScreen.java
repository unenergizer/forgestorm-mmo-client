package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameSkin;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.screens.stage.LoginScreenUI;
import com.valenguard.client.game.screens.stage.UiManager;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginScreen extends ScreenAdapter {

    private static final boolean PRINT_DEBUG = false;

    private UiManager uiManager;
    private FileManager fileManager;
    private SpriteBatch spriteBatch;

    @Override
    public void show() {
        Log.println(getClass(), "Invoked: show()", false, PRINT_DEBUG);

        spriteBatch = new SpriteBatch();
        fileManager = Valenguard.getInstance().getFileManager();

        // Load assets
        fileManager.loadTexture(GameTexture.LOGIN_BACKGROUND);

        // Change cursor
        fileManager.loadPixmap(GamePixmap.CURSOR_1);
        Pixmap pixmap = fileManager.getPixmap(GamePixmap.CURSOR_1);
        Cursor customCursor = Gdx.graphics.newCursor(pixmap, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
        Gdx.graphics.setCursor(customCursor);

        // Show UI
        uiManager = Valenguard.getInstance().getUiManager();
        uiManager.setup(new ScreenViewport(), GameSkin.DEFAULT);
        uiManager.addUi("login", new LoginScreenUI(), true);
        uiManager.show("login");

        // Setup input controls
        Gdx.input.setInputProcessor(uiManager.getStage());
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen();

        // Draw textures
        spriteBatch.begin();
        spriteBatch.draw(fileManager.getTexture(GameTexture.LOGIN_BACKGROUND), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        // Render UI
        uiManager.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Log.println(getClass(), "Invoked: resize(w: " + width + ", h: " + height + ")", false, PRINT_DEBUG);
        uiManager.resize(width, height);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        Log.println(getClass(), "Invoked: dispose()", false, PRINT_DEBUG);
        if (uiManager != null) uiManager.removeAllUi();
        if (spriteBatch != null) spriteBatch.dispose();
        if (fileManager != null) fileManager.dispose();
    }
}