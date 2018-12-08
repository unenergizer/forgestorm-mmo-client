package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.util.GraphicsUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginScreen extends ScreenAdapter {

    private final FileManager fileManager = Valenguard.getInstance().getFileManager();
    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();
    private SpriteBatch spriteBatch;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        // Load assets
        fileManager.loadAtlas(GameAtlas.ITEM_TEXTURES);
        fileManager.loadTexture(GameTexture.LOGIN_BACKGROUND);

        // Change cursor
        fileManager.loadPixmap(GamePixmap.CURSOR_1);
        Pixmap pixmap = fileManager.getPixmap(GamePixmap.CURSOR_1);
        Cursor customCursor = Gdx.graphics.newCursor(pixmap, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
        Gdx.graphics.setCursor(customCursor);

        // Show UI
        stageHandler.init();
        stageHandler.getLoginTable().setVisible(true);
        stageHandler.getButtonTable().setVisible(true);
        stageHandler.getCopyrightTable().setVisible(true);
        stageHandler.getVersionTable().setVisible(true);

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageHandler.getPreStageEvent());
        multiplexer.addProcessor(stageHandler.getStage());
        multiplexer.addProcessor(stageHandler.getPostStageEvent());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen();

        // Draw textures
        spriteBatch.begin();
        spriteBatch.draw(fileManager.getTexture(GameTexture.LOGIN_BACKGROUND), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        // Render UI
        stageHandler.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        stageHandler.resize(width, height);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) {
            fileManager.unloadAsset(GameTexture.LOGIN_BACKGROUND.getFilePath());
            spriteBatch.dispose();
        }
    }
}