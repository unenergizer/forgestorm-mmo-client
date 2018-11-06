package com.valenguard.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.assets.FileManager;
import com.valenguard.client.assets.GameTexture;
import com.valenguard.client.assets.GameUI;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.input.Keyboard;
import com.valenguard.client.input.Mouse;
import com.valenguard.client.screens.stage.UiManager;
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GameScreen implements Screen {

    private FileManager fileManager;

    private SpriteBatch spriteBatch;

    private AttachableCamera camera;
    private ScreenViewport screenViewport;

    private Stage stage;
    private Skin skin;

    @Setter
    private String gameMapNameFromServer;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // TODO: RELOCATE
    private Texture playerTexture;
    private Texture otherPlayerTexture;
    private Texture redTileTexture;
    private BitmapFont font;

    @Override
    public void show() {
        fileManager = Valenguard.getInstance().getFileManager();
        spriteBatch = new SpriteBatch();
//        font = new BitmapFont(Gdx.files.internal("font/testfont.fnt"), false);
        fileManager.loadFont("font/testfont.fnt");
        font = fileManager.getFont("font/testfont.fnt");

        fileManager.loadAtlas("atlas/running.atlas");

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_WIDTH, ClientConstants.SCREEN_HEIGHT, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();
        stage = new Stage(screenViewport);

        // Attach entity to camera
        camera.attachEntity(EntityManager.getInstance().getPlayerClient());
        camera.zoom = ClientConstants.ZOOM_DEFAULT;

        // Map setup
        String mapFilePath = ClientConstants.MAP_DIRECTORY + "/" + gameMapNameFromServer + ".tmx";
        setTiledMap(mapFilePath);

        // Load assets
        skin = new Skin(Gdx.files.internal(GameUI.UI_SKIN.getFilePath()));
        fileManager.loadTexture(GameTexture.TEMP_PLAYER_IMG);
        playerTexture = fileManager.getTexture(GameTexture.TEMP_PLAYER_IMG);
        fileManager.loadTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);
        otherPlayerTexture = fileManager.getTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);
        fileManager.loadTexture(GameTexture.REDTILE);
        redTileTexture = fileManager.getTexture(GameTexture.REDTILE);
        fileManager.loadPixmap("cursor1.png");
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(fileManager.getPixmap("cursor1.png"), 0, 0));

        // Show UI
        UiManager uiManager = Valenguard.getInstance().getUiManager();
        uiManager.setup(stage, skin);
//        uiManager.show(new GameScreenDebugText()); // DEBUG

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new Keyboard());
        multiplexer.addProcessor(new Mouse());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(.05f, 0f, .12f, 1);


        if (mapRenderer != null && tiledMap != null) {
            // Update camera
            camera.clampCamera(screenViewport, tiledMap);
            camera.update();

            // Render tiledMap
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        // Draw textures
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        Queue<PathFinding.MoveNode> p = Valenguard.getInstance().getMouseManager().getMoveNodes();
        if (p != null) {
            for (PathFinding.MoveNode pathNodes : p) {
                spriteBatch.draw(redTileTexture, pathNodes.getWorldX() * ClientConstants.TILE_SIZE, pathNodes.getWorldY() * ClientConstants.TILE_SIZE);
            }
        }

//        spriteBatch.draw(redTileTexture, Valenguard.getInstance().getMouseManager().getMouseTileX() * ClientConstants.TILE_SIZE, Valenguard.getInstance().getMouseManager().getMouseTileY() * ClientConstants.TILE_SIZE);

        for (Entity entity : EntityManager.getInstance().getEntities().values()) {
            if (!(entity instanceof PlayerClient)) {

                entity.animate(delta, spriteBatch);

                float x = entity.getDrawX() + (playerTexture.getWidth() / 2f);
                float y = entity.getDrawY() + (playerTexture.getHeight() + ClientConstants.namePlateDistanceInPixels);
                final GlyphLayout layout = new GlyphLayout(font, Integer.toString(entity.getServerEntityID()));
                font.setColor(Color.YELLOW);
                font.draw(spriteBatch, layout, x - (layout.width / 2), y);
//                font.setColor(Color.YELLOW);
//                font.draw(spriteBatch, layout, x - (layout.width / 2) - 1, y + 1);
            } else {

                entity.animate(delta, spriteBatch);

                float x = entity.getDrawX() + (playerTexture.getWidth() / 2f);
                float y = entity.getDrawY() + (playerTexture.getHeight() + ClientConstants.namePlateDistanceInPixels);
                final GlyphLayout layout = new GlyphLayout(font, Integer.toString(entity.getServerEntityID()));
                font.setColor(Color.YELLOW);
                font.draw(spriteBatch, layout, x - (layout.width / 2), y);
//                font.setColor(Color.YELLOW);
//                font.draw(spriteBatch, layout, x - (layout.width / 2) - 1, y + 1);
            }
        }

        spriteBatch.end();

        // Render UI
        Valenguard.getInstance().getUiManager().refreshAbstractUi();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();


        // Update game logic
        Valenguard.getInstance().getClientPlayerMovementManager().tick(delta);
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
        fileManager.unloadAsset(GameTexture.REDTILE.getFilePath());
    }

    /**
     * Sets the tiled map to be rendered.
     *
     * @param mapFilePath The tiled map based on name
     */
    public void setTiledMap(String mapFilePath) {
        System.out.println("[GameScreen] settingTiledMap[" + mapFilePath + "]");
        fileManager.loadTiledMap(mapFilePath);
        tiledMap = fileManager.getTiledMap(mapFilePath);

        if (mapRenderer == null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        } else {
            mapRenderer.setMap(fileManager.getTiledMap(mapFilePath));
        }
    }
}
