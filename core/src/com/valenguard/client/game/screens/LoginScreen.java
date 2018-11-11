package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.assets.GameUI;
import com.valenguard.client.game.screens.stage.UiManager;
import com.valenguard.client.util.GraphicsUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginScreen extends ScreenAdapter {

    private UiManager uiManager;
    private FileManager fileManager;

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        fileManager = Valenguard.getInstance().getFileManager();

        // Setup Camera
        stage = new Stage(new ScreenViewport());

        // Load assets
        skin = new Skin(Gdx.files.internal(GameUI.UI_SKIN.getFilePath()));
        fileManager.loadTexture(GameTexture.LOGIN_BACKGROUND);

        // Change cursor
        fileManager.loadPixmap(GamePixmap.CURSOR_1);
        Pixmap pixmap = fileManager.getPixmap(GamePixmap.CURSOR_1);
        Cursor customCursor = Gdx.graphics.newCursor(pixmap, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
        Gdx.graphics.setCursor(customCursor);

        // Show UI
        uiManager = Valenguard.getInstance().getUiManager();
        uiManager.setup(stage, skin);
        uiManager.show(new com.valenguard.client.game.screens.stage.LoginScreenUI());

        // Setup input controls
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen();

        // Draw textures
        spriteBatch.begin();
        spriteBatch.draw(fileManager.getTexture(GameTexture.LOGIN_BACKGROUND), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        // Render UI
        uiManager.refreshAbstractUi();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        uiManager.dispose();
        if (skin != null) skin.dispose();
        if (stage != null) stage.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        fileManager.unloadAsset(GameTexture.LOGIN_BACKGROUND.getFilePath());
    }
}