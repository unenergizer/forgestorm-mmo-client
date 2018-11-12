package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.assets.GameFont;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.assets.GameUI;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.input.Keyboard;
import com.valenguard.client.game.input.Mouse;
import com.valenguard.client.game.maps.MapUtil;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.screens.stage.UiManager;
import com.valenguard.client.network.PlayerSessionData;
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.Log;
import com.valenguard.client.util.MoveNode;

import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GameScreen implements Screen {

    private static final boolean PRINT_DEBUG = false;

    private FileManager fileManager;
    private SpriteBatch spriteBatch;

    private AttachableCamera camera;
    private ScreenViewport screenViewport;

    // TODO: RELOCATE
    private Stage stage;
    private Skin skin;

    @Setter
    private String gameMapNameFromServer;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // TODO: RELOCATE
    private Texture playerTexture;
    private Texture otherPlayerTexture;
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
        fileManager = Valenguard.getInstance().getFileManager();
        spriteBatch = new SpriteBatch();

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_WIDTH, ClientConstants.SCREEN_HEIGHT, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();
        stage = new Stage(screenViewport);

        // Load assets
        fileManager.loadFont(GameFont.TEST_FONT);
        font = fileManager.getFont(GameFont.TEST_FONT);
        fileManager.loadAtlas(GameAtlas.MAIN_ATLAS);
        fileManager.loadTexture(GameTexture.TEMP_PLAYER_IMG);
        playerTexture = fileManager.getTexture(GameTexture.TEMP_PLAYER_IMG);
        fileManager.loadTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);
        otherPlayerTexture = fileManager.getTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);
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
        skin = new Skin(Gdx.files.internal(GameUI.UI_SKIN.getFilePath()));
        UiManager uiManager = Valenguard.getInstance().getUiManager();
        uiManager.setup(stage, skin);

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(keyboard);
        multiplexer.addProcessor(new Mouse());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(21f, 21f, 21f, 0);

        if (EntityManager.getInstance().getPlayerClient() == null) return;

        tickGameLogic(delta);

        if (mapRenderer == null && tiledMap == null) return;
        // Update camera
        camera.clampCamera(screenViewport, tiledMap);
        camera.update();

        // Render tiledMap
        mapRenderer.setView(camera);
        mapRenderer.getBatch().begin();
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer.getName().equals("overhead")) continue;
            if (!(layer instanceof TiledMapTileLayer)) continue;
            mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
        }
        mapRenderer.getBatch().end();


        // Draw textures
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        if (Valenguard.getInstance().getClientMovementProcessor().getCurrentMovementInput() == ClientMovementProcessor.MovementInput.MOUSE) {
            Queue<MoveNode> remainingMoveNodes = Valenguard.getInstance().getClientPlayerMovementManager().getMovements();
            for (MoveNode moveNode : remainingMoveNodes) {
                spriteBatch.draw(tilePathTexture, moveNode.getWorldX() * ClientConstants.TILE_SIZE, moveNode.getWorldY() * ClientConstants.TILE_SIZE);
            }
        }

        for (MovingEntity entity : EntityManager.getInstance().getEntities().values()) {
            entity.animate(delta, spriteBatch);
            float x = entity.getDrawX() + (playerTexture.getWidth() / 2f);
            float y = entity.getDrawY() + (playerTexture.getHeight() + ClientConstants.namePlateDistanceInPixels);
            final GlyphLayout layout;

            if (entity.getEntityType() == EntityType.NPC) {
                font.setColor(Color.LIME);
                layout = new GlyphLayout(font, "[NPC] " + entity.getEntityName());
            } else {
                font.setColor(Color.YELLOW);
                layout = new GlyphLayout(font, Integer.toString(entity.getServerEntityID()));
            }
            font.draw(spriteBatch, layout, x - (layout.width / 2), y);
        }

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        playerClient.animate(delta, spriteBatch);

        float x = playerClient.getDrawX() + (playerTexture.getWidth() / 2f);
        float y = playerClient.getDrawY() + (playerTexture.getHeight() + ClientConstants.namePlateDistanceInPixels);
        font.setColor(Color.YELLOW);
        final GlyphLayout layout = new GlyphLayout(font, Integer.toString(playerClient.getServerEntityID()));
        font.draw(spriteBatch, layout, x - (layout.width / 2), y);

        // Draw mouse
        if (!MapUtil.isTraversable(playerClient.getGameMap(), Valenguard.getInstance().getMouseManager().getMouseTileX(), Valenguard.getInstance().getMouseManager().getMouseTileY())) {
            spriteBatch.draw(invalidMoveLocation, Valenguard.getInstance().getMouseManager().getMouseScreenX() - 8, Valenguard.getInstance().getMouseManager().getMouseScreenY() - 8);
        } else if (MapUtil.isWarp(playerClient.getGameMap(), Valenguard.getInstance().getMouseManager().getMouseTileX(), Valenguard.getInstance().getMouseManager().getMouseTileY())) {
            spriteBatch.draw(warpLocation, Valenguard.getInstance().getMouseManager().getMouseScreenX() - 8, Valenguard.getInstance().getMouseManager().getMouseScreenY() - 8);
        } else if (MapUtil.isOutOfBounds(playerClient.getGameMap(), Valenguard.getInstance().getMouseManager().getMouseTileX(), Valenguard.getInstance().getMouseManager().getMouseTileY())) {
            spriteBatch.draw(invalidMoveLocation, Valenguard.getInstance().getMouseManager().getMouseScreenX() - 8, Valenguard.getInstance().getMouseManager().getMouseScreenY() - 8);
        }
        spriteBatch.end();

        // Draw overhead map layer
        mapRenderer.getBatch().begin();
        if (mapRenderer != null && tiledMap != null) {
            mapRenderer.renderTileLayer((TiledMapTileLayer) tiledMap.getLayers().get("overhead"));
        }
        mapRenderer.getBatch().end();

        // Render UI
        Valenguard.getInstance().getUiManager().refreshAbstractUi();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void tickGameLogic(float delta) {
        // Update game logic
        Valenguard.getInstance().getClientMovementProcessor().processMovement(EntityManager.getInstance().getPlayerClient());
        Valenguard.getInstance().getClientPlayerMovementManager().processMoveNodes(EntityManager.getInstance().getPlayerClient(), delta);
        Valenguard.getInstance().getEntityMovementManager().tick(delta);
    }

    @Override
    public void resize(int width, int height) {
        screenViewport.update(width, height, true);
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
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
        Valenguard.getInstance().getUiManager().dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        fileManager.unloadAsset(GameTexture.TEMP_PLAYER_IMG.getFilePath());
        fileManager.unloadAsset(GameTexture.TEMP_OTHER_PLAYER_IMG.getFilePath());
        fileManager.unloadAsset(GameTexture.TILE_PATH.getFilePath());
    }

    /**
     * Sets the tiled map to be rendered.
     *
     * @param mapName The tiled map based on name
     */
    public void setTiledMap(String mapName) {
        gameMapNameFromServer = mapName;
        String filePath = ClientConstants.MAP_DIRECTORY + "/" + mapName + ".tmx";
        Log.println(getClass(), "Map Path: " + filePath, false, PRINT_DEBUG);
        Log.println(getClass(), "Map Name: " + mapName, false, PRINT_DEBUG);
        fileManager.loadTiledMap(filePath);
        tiledMap = fileManager.getTiledMap(filePath);

        if (mapRenderer == null) mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        else mapRenderer.setMap(fileManager.getTiledMap(filePath));
    }
}
