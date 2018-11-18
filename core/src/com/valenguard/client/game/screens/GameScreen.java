package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.assets.GameFont;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameSkin;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.input.Keyboard;
import com.valenguard.client.game.input.Mouse;
import com.valenguard.client.game.maps.MapRenderer;
import com.valenguard.client.game.screens.stage.UiManager;
import com.valenguard.client.game.screens.stage.game.ChatBox;
import com.valenguard.client.network.PlayerSessionData;
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.Log;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GameScreen implements Screen {

    private static final boolean PRINT_DEBUG = false;

    private UiManager uiManager;
    private FileManager fileManager;
    private SpriteBatch spriteBatch;

    private AttachableCamera camera;
    private ScreenViewport screenViewport;

    private MapRenderer mapRenderer = new MapRenderer();

    // TODO: RELOCATE
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

        fileManager = Valenguard.getInstance().getFileManager();
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
        fileManager.loadTexture(GameTexture.TILE_PATH);

        tilePathTexture = fileManager.getTexture(GameTexture.TILE_PATH);
        fileManager.loadTexture(GameTexture.INVALID_MOVE);
        invalidMoveLocation = fileManager.getTexture(GameTexture.INVALID_MOVE);
        fileManager.loadTexture(GameTexture.WARP_LOCATION);
        warpLocation = fileManager.getTexture(GameTexture.WARP_LOCATION);

        // Change mouse cursor
        fileManager.loadPixmap(GamePixmap.CURSOR_1);
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(fileManager.getPixmap(GamePixmap.CURSOR_1), 0, 0));

        // Show UI
        uiManager = Valenguard.getInstance().getUiManager();
        uiManager.setup(screenViewport, GameSkin.DEFAULT);
        uiManager.addUi("chatbox", new ChatBox(), true);

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(keyboard);
        multiplexer.addProcessor(new Mouse());
        multiplexer.addProcessor(uiManager.getStage());
        Gdx.input.setInputProcessor(multiplexer);
    }


    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(21f, 21f, 21f, 0);

        if (EntityManager.getInstance().getPlayerClient() == null) return;
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        tickGameLogic(delta);

        if (!mapRenderer.isReadyToRender()) return;
        camera.clampCamera(screenViewport, mapRenderer.getTiledMap());
        camera.update();

        mapRenderer.renderBottomMapLayers(camera);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Valenguard.getInstance().getMouseManager().drawMoveNodes(spriteBatch, tilePathTexture);
        EntityManager.getInstance().drawEntities(delta, spriteBatch);
        playerClient.getEntityAnimation().animate(delta, spriteBatch);
        playerClient.drawEntityName();
        spriteBatch.end();

        mapRenderer.renderOverheadMapLayers();
        Valenguard.getInstance().getMouseManager().drawMovingMouse(playerClient, spriteBatch, invalidMoveLocation, warpLocation);
        Valenguard.getInstance().getUiManager().render(delta);
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
        uiManager.resize(width, height);
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
        if (uiManager != null) uiManager.removeAllUi();
        if (fileManager != null) fileManager.dispose();
    }
}
