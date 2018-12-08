package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.FocusManager;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.assets.GameFont;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.input.Keyboard;
import com.valenguard.client.game.input.Mouse;
import com.valenguard.client.game.maps.MapRenderer;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.network.PlayerSessionData;
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GameScreen implements Screen {

    private static final boolean PRINT_DEBUG = true;

    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();
    private final FileManager fileManager = Valenguard.getInstance().getFileManager();

    private MapRenderer mapRenderer = new MapRenderer();
    private AttachableCamera camera;
    private ScreenViewport screenViewport;

    // TODO: RELOCATE
    private SpriteBatch spriteBatch;
    private Texture parallaxBackground;
    private Texture tilePathTexture;
    private Texture invalidMoveLocation;
    private Texture warpLocation;
    private BitmapFont font;

    @Setter
    @Getter
    private PlayerSessionData playerSessionData;

    // TODO: RELOCATE
    private Keyboard keyboard = new Keyboard();

    @Override
    public void show() {
        Log.println(getClass(), "Invoked: show()", false, PRINT_DEBUG);
        spriteBatch = new SpriteBatch();

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_WIDTH, ClientConstants.SCREEN_HEIGHT, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();

        // Load assets
        fileManager.loadFont(GameFont.TEST_FONT);
        font = fileManager.getFont(GameFont.TEST_FONT);
        font.setUseIntegerPositions(false);

        fileManager.loadAtlas(GameAtlas.ENTITY_CHARACTER);
        fileManager.loadAtlas(GameAtlas.ENTITY_MONSTER);

        fileManager.loadTexture(GameTexture.PARALLAX_BACKGROUND);
        parallaxBackground = fileManager.getTexture(GameTexture.PARALLAX_BACKGROUND);
        parallaxBackground.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        fileManager.loadTexture(GameTexture.TILE_PATH);
        tilePathTexture = fileManager.getTexture(GameTexture.TILE_PATH);
        fileManager.loadTexture(GameTexture.INVALID_MOVE);
        invalidMoveLocation = fileManager.getTexture(GameTexture.INVALID_MOVE);
        fileManager.loadTexture(GameTexture.WARP_LOCATION);
        warpLocation = fileManager.getTexture(GameTexture.WARP_LOCATION);

        // Change mouse cursor
        fileManager.loadPixmap(GamePixmap.CURSOR_1);
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(fileManager.getPixmap(GamePixmap.CURSOR_1), 0, 0));

        // User Interface
        FocusManager.resetFocus(stageHandler.getStage());

        // --- hide ui ---
        stageHandler.getLoginTable().setVisible(false);
        stageHandler.getButtonTable().setVisible(false);
        stageHandler.getCopyrightTable().setVisible(false);
        stageHandler.getVersionTable().setVisible(false);
        stageHandler.getMainSettingsWindow().setVisible(false);

        // --- show ui ---
        stageHandler.getChatWindow().fadeIn().setVisible(true);

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageHandler.getPreStageEvent());
        multiplexer.addProcessor(stageHandler.getStage());
        multiplexer.addProcessor(stageHandler.getPostStageEvent());
        multiplexer.addProcessor(keyboard);
        multiplexer.addProcessor(new Mouse());
        Gdx.input.setInputProcessor(multiplexer);
    }

    int srcX = 0;
    int srcY = 0;

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(21f, 21f, 21f, 0);

        if (EntityManager.getInstance().getPlayerClient() == null) return;
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        tickGameLogic(delta);

        if (!mapRenderer.isReadyToRender()) return;
        camera.clampCamera(screenViewport, mapRenderer.getTiledMap());
        camera.update();

        if (mapRenderer.getGameMapNameFromServer().equals("floating_island")) {
            spriteBatch.begin();
            srcX += 2;
            srcY -= 3;
            if (srcX >= parallaxBackground.getWidth()) srcX = 0;
            if (srcY <= -parallaxBackground.getHeight()) srcY = 0;
            spriteBatch.draw(parallaxBackground, 0, 0, srcX, srcY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }

        mapRenderer.renderBottomMapLayers(camera);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Valenguard.getInstance().getMouseManager().drawMoveNodes(spriteBatch, tilePathTexture);
        EntityManager.getInstance().drawEntityBodies(delta, spriteBatch);
        playerClient.getEntityAnimation().animate(delta, spriteBatch);
        EntityManager.getInstance().drawEntityNames(delta, spriteBatch);
        playerClient.drawEntityName();
        spriteBatch.end();

        mapRenderer.renderOverheadMapLayers();
        Valenguard.getInstance().getMouseManager().drawMovingMouse(playerClient, spriteBatch, invalidMoveLocation, warpLocation);
        Valenguard.getInstance().getStageHandler().render(delta);
    }

    private void tickGameLogic(float delta) {
        Valenguard.getInstance().getClientMovementProcessor().processMovement(EntityManager.getInstance().getPlayerClient());
        Valenguard.getInstance().getClientPlayerMovementManager().processMoveNodes(EntityManager.getInstance().getPlayerClient(), delta);
        Valenguard.getInstance().getEntityMovementManager().tick(delta);
    }

    @Override
    public void resize(int width, int height) {
        Log.println(getClass(), "Invoked: resize(w: " + width + ", h: " + height + ")", false, PRINT_DEBUG);
        camera.setToOrtho(false, width, height);
        stageHandler.resize(width, height);
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
        Log.println(getClass(), "Invoked: dispose()", false, PRINT_DEBUG);
        if (mapRenderer != null) mapRenderer.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
    }
}
