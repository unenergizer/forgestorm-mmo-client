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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.assets.GameFont;
import com.valenguard.client.game.assets.GamePixmap;
import com.valenguard.client.game.assets.GameSkin;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.entities.Entity;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.EntityType;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.input.Keyboard;
import com.valenguard.client.game.input.Mouse;
import com.valenguard.client.game.maps.MapUtil;
import com.valenguard.client.game.movement.ClientMovementProcessor;
import com.valenguard.client.game.screens.stage.UiManager;
import com.valenguard.client.game.screens.stage.game.ChatBox;
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

    private UiManager uiManager;
    private FileManager fileManager;
    private SpriteBatch spriteBatch;

    private AttachableCamera camera;
    private ScreenViewport screenViewport;

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
        Log.println(getClass(), "Invoked: show()", false, PRINT_DEBUG);

        fileManager = Valenguard.getInstance().getFileManager();
        spriteBatch = new SpriteBatch();

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_WIDTH, ClientConstants.SCREEN_HEIGHT, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();

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

//        ((ChatBox)Valenguard.getInstance().getUiManager().getAbstractUI("chatbox")).updateChatBox("delta: " + delta);

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
//            drawEntityName(entity);
        }

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        playerClient.animate(delta, spriteBatch);

//        float x = playerClient.getDrawX() + (playerTexture.getWidth() / 2f);
//        float y = playerClient.getDrawY() + (playerTexture.getHeight() + ClientConstants.namePlateDistanceInPixels);
//        font.setColor(Color.YELLOW);
//        final GlyphLayout layout = new GlyphLayout(font, Integer.toString(playerClient.getServerEntityID()));
//        font.draw(spriteBatch, layout, x - (layout.width / 2), y);

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

        Valenguard.getInstance().getUiManager().render(delta);
    }

    private GlyphLayout layout1 = null;
    private GlyphLayout layout2 = null;

    private void drawEntityName(Entity entity) {
        float x = entity.getDrawX() + (playerTexture.getWidth() / 2f);
        float y = entity.getDrawY() + (playerTexture.getHeight() + ClientConstants.namePlateDistanceInPixels);


        if (entity.getEntityType() == EntityType.NPC) {
            font.setColor(Color.BLACK);
            layout2 = new GlyphLayout(font, entity.getEntityName());
            font.setColor(Color.LIME);
            layout1 = new GlyphLayout(font, entity.getEntityName());
        } else {
            font.setColor(Color.BLACK);
            layout2 = new GlyphLayout(font, entity.getEntityName());
            font.setColor(Color.GOLD);
            layout1 = new GlyphLayout(font, entity.getEntityName());
        }

        font.setColor(Color.BLACK);
        font.draw(spriteBatch, layout2, x - (layout2.width / 2) + .8f, y - .8f);

        font.setColor(Color.GOLD);
        font.draw(spriteBatch, layout1, x - (layout1.width / 2), y);
    }

    private void tickGameLogic(float delta) {
        // Update game logic
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
