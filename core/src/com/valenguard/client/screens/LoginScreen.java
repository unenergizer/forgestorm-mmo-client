package com.valenguard.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.Valenguard;
import com.valenguard.client.assets.FileManager;
import com.valenguard.client.assets.GameTexture;
import com.valenguard.client.assets.GameUI;
import com.valenguard.client.screens.stage.LoginScreenUI;
import com.valenguard.client.screens.stage.UiManager;
import com.valenguard.client.util.GraphicsUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginScreen extends ScreenAdapter {

    private static final String TAG = LoginScreen.class.getSimpleName();

    private SpriteBatch spriteBatch;
    private FileManager fileManager;
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

        // Show UI
        UiManager uiManager = Valenguard.getInstance().getUiManager();
        uiManager.setup(stage, skin);
        uiManager.show(new LoginScreenUI());

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
        Valenguard.getInstance().getUiManager().refreshAbstractUi();
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
        Valenguard.getInstance().getUiManager().dispose();
        if (skin != null) skin.dispose();
        if (stage != null) stage.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        fileManager.unloadAsset(GameTexture.LOGIN_BACKGROUND.getFilePath());
    }
}