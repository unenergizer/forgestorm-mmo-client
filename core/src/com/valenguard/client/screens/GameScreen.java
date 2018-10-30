package com.valenguard.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.assets.FileManager;
import com.valenguard.client.assets.GameMap;
import com.valenguard.client.assets.GameTexture;
import com.valenguard.client.assets.GameUI;
import com.valenguard.client.entities.Entity;
import com.valenguard.client.entities.EntityManager;
import com.valenguard.client.entities.PlayerClient;
import com.valenguard.client.input.Keyboard;
import com.valenguard.client.input.Mouse;
import com.valenguard.client.screens.stage.GameScreenDebugText;
import com.valenguard.client.screens.stage.UiManager;
import com.valenguard.client.util.AttachableCamera;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.pathfinding.PathFinding;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GameScreen implements Screen {

    private SpriteBatch spriteBatch;

    private AttachableCamera camera;
    private ScreenViewport screenViewport;

    private Stage stage;
    private Skin skin;

    @Setter
    private GameMap gameMap;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // TODO: RELOCATE
    private Texture playerTexture;
    private Texture otherPlayerTexture;
    private Texture redTileTexture;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_WIDTH, ClientConstants.SCREEN_HEIGHT, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();
        stage = new Stage(screenViewport);

        // Attach entity to camera
        camera.attachEntity(EntityManager.getInstance().getPlayerClient());
        camera.zoom = ClientConstants.ZOOM_DEFAULT;

        // Map setup
        setTiledMap(gameMap);

        // Load assets
        skin = new Skin(Gdx.files.internal(GameUI.UI_SKIN.getFilePath()));
        Valenguard.getInstance().getFileManager().loadTexture(GameTexture.TEMP_PLAYER_IMG);
        playerTexture = Valenguard.getInstance().getFileManager().getTexture(GameTexture.TEMP_PLAYER_IMG);
        Valenguard.getInstance().getFileManager().loadTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);
        otherPlayerTexture = Valenguard.getInstance().getFileManager().getTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);
        Valenguard.getInstance().getFileManager().loadTexture(GameTexture.REDTILE);
        redTileTexture = Valenguard.getInstance().getFileManager().getTexture(GameTexture.REDTILE);

        // Show UI
        UiManager uiManager = Valenguard.getInstance().getUiManager();
        uiManager.setup(stage, skin);
        uiManager.show(new GameScreenDebugText());

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new Keyboard());
        multiplexer.addProcessor(new Mouse());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(.05f, 0f, .12f, 1);

        // Update game logic
        Valenguard.getInstance().getClientPlayerMovementManager().tick(delta);
        Valenguard.getInstance().getEntityMovementManager().tick(delta);

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

        List<PathFinding.MoveNode> p = Valenguard.getInstance().getMouseManager().getMoveNodes();
        if (p != null) {
            for (PathFinding.MoveNode penisNode : p) {
                spriteBatch.draw(redTileTexture, penisNode.getWorldX() * ClientConstants.TILE_SIZE, penisNode.getWorldY() * ClientConstants.TILE_SIZE);
            }
        }

        spriteBatch.draw(redTileTexture, Valenguard.getInstance().getMouseManager().getMouseTileX() * ClientConstants.TILE_SIZE, Valenguard.getInstance().getMouseManager().getMouseTileY() * ClientConstants.TILE_SIZE);

        for (Entity entity : EntityManager.getInstance().getEntities().values()) {
            if (entity instanceof PlayerClient) {
                spriteBatch.draw(playerTexture, entity.getDrawX(), entity.getDrawY());
            } else {
                spriteBatch.draw(otherPlayerTexture, entity.getDrawX(), entity.getDrawY());
            }
        }

        spriteBatch.end();

        // Render UI
        Valenguard.getInstance().getUiManager().refreshAbstractUi();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
        FileManager fileManager = Valenguard.getInstance().getFileManager();
        fileManager.unloadAsset(GameTexture.TEMP_PLAYER_IMG.getFilePath());
        fileManager.unloadAsset(GameTexture.TEMP_OTHER_PLAYER_IMG.getFilePath());
        fileManager.unloadAsset(GameTexture.REDTILE.getFilePath());
    }

    /**
     * Sets the tiled map to be rendered.
     *
     * @param gameMap The tiled map based on name
     */
    private void setTiledMap(GameMap gameMap) {
        if (gameMap == null) return;
        FileManager fileManager = Valenguard.getInstance().getFileManager();
        fileManager.loadTiledMap(gameMap);
        tiledMap = fileManager.getTiledMap(gameMap);

        if (mapRenderer == null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        } else {
            mapRenderer.setMap(fileManager.getTiledMap(gameMap));
        }
    }
}
