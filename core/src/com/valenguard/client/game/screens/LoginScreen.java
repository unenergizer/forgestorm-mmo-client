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
import com.valenguard.client.game.assets.GameMusic;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.audio.MusicManager;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginScreen extends ScreenAdapter {

    private static final boolean PRINT_DEBUG = true;

    private final FileManager fileManager = Valenguard.getInstance().getFileManager();
    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();
    private SpriteBatch spriteBatch;
    private Pixmap cursorPixmap;
    private Cursor cursor;

    @Override
    public void show() {
        Log.println(getClass(), "Invoked: show()", false, PRINT_DEBUG);
        spriteBatch = new SpriteBatch();
        MusicManager musicManager = Valenguard.getInstance().getMusicManager();

        // Load assets
        fileManager.loadAtlas(GameAtlas.ITEM_TEXTURES);
        fileManager.loadTexture(GameTexture.LOGIN_BACKGROUND);

        // Change cursor
        fileManager.loadPixmap(GamePixmap.CURSOR_1);
        cursorPixmap = fileManager.getPixmap(GamePixmap.CURSOR_1);
        cursor = Gdx.graphics.newCursor(cursorPixmap, cursorPixmap.getWidth() / 2, cursorPixmap.getHeight() / 2);
        Gdx.graphics.setCursor(cursor);

        // User Interface
        stageHandler.init(null);

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageHandler.getPreStageEvent());
        multiplexer.addProcessor(stageHandler.getStage());
        multiplexer.addProcessor(stageHandler.getPostStageEvent());
        Gdx.input.setInputProcessor(multiplexer);

        // Play audio
        if (musicManager.getAudioPreferences().isPlayLoginScreenMusic())
            musicManager.playSong(GameMusic.LOGIN_SCREEN_THEME_1);
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
        Log.println(getClass(), "Invoked: resize(w: " + width + ", h: " + height + ")", false, PRINT_DEBUG);
        stageHandler.resize(width, height);
    }

    @Override
    public void pause() {
        Log.println(getClass(), "Invoked: pause()", false, PRINT_DEBUG);
        Valenguard.getInstance().getMusicManager().pauseMusic();
    }

    @Override
    public void resume() {
        Log.println(getClass(), "Invoked: resume()", false, PRINT_DEBUG);
        final MusicManager musicManager = Valenguard.getInstance().getMusicManager();
        if (musicManager.getAudioPreferences().isPlayLoginScreenMusic()) musicManager.resumeMusic();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        Log.println(getClass(), "Invoked: dispose()", false, PRINT_DEBUG);
        if (spriteBatch != null) {
            fileManager.unloadAsset(GameTexture.LOGIN_BACKGROUND.getFilePath());
            spriteBatch.dispose();
            spriteBatch = null;
        }
        if (cursor != null) {
            cursor.dispose();
            cursor = null;
        }
        Valenguard.getInstance().getStageHandler().dispose();
    }
}