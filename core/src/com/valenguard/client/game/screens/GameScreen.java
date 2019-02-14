package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.FileManager;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.assets.GameFont;
import com.valenguard.client.game.assets.GameTexture;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.input.Keyboard;
import com.valenguard.client.game.input.Mouse;
import com.valenguard.client.game.maps.MapRenderer;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.network.PlayerSessionData;
import com.valenguard.client.util.GraphicsUtils;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

@Getter
public class GameScreen implements Screen {

    private static final boolean PRINT_DEBUG = false;

    private final StageHandler stageHandler = Valenguard.getInstance().getStageHandler();
    private final FileManager fileManager = Valenguard.getInstance().getFileManager();

    private MapRenderer mapRenderer = new MapRenderer();
    private AttachableCamera camera;
    private ScreenViewport screenViewport;

    private boolean gameFocused = true;

    // TODO: RELOCATE
    private SpriteBatch spriteBatch;
    private Texture parallaxBackground;
    private BitmapFont font;

    @Setter
    private PlayerSessionData playerSessionData;

    // TODO: RELOCATE
    private Keyboard keyboard = new Keyboard();

    @Override
    public void show() {
        println(getClass(), "Invoked: show()", false, PRINT_DEBUG);
        spriteBatch = new SpriteBatch();

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_WIDTH, ClientConstants.SCREEN_HEIGHT, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();

        // Load assets
        fileManager.loadFont(GameFont.TEST_FONT);
        font = fileManager.getFont(GameFont.TEST_FONT);
        font.setUseIntegerPositions(false);

        fileManager.loadAtlas(GameAtlas.CURSOR);
        fileManager.loadAtlas(GameAtlas.ENTITY_CHARACTER);
        fileManager.loadAtlas(GameAtlas.ENTITY_MONSTER);
        fileManager.loadAtlas(GameAtlas.SKILL_NODES);

        fileManager.loadTexture(GameTexture.PARALLAX_BACKGROUND);
        parallaxBackground = fileManager.getTexture(GameTexture.PARALLAX_BACKGROUND);
        parallaxBackground.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Change mouse cursor
//        fileManager.loadPixmap(GamePixmap.CURSOR_1);
//        Gdx.graphics.setCursor(Gdx.graphics.newCursor(fileManager.getPixmap(GamePixmap.CURSOR_1), 0, 0));

        // User Interface
        stageHandler.dispose();
        stageHandler.init(screenViewport);

        // Setup input controls
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageHandler.getPreStageEvent());
        multiplexer.addProcessor(stageHandler.getStage());
        multiplexer.addProcessor(stageHandler.getPostStageEvent());
        multiplexer.addProcessor(keyboard);
        multiplexer.addProcessor(new Mouse());
        Gdx.input.setInputProcessor(multiplexer);

        // Handle Music
        Valenguard.getInstance().getMusicManager().stopSong(true);
    }

    private int srcX = 0; //TODO RELOCATE PARALLAX BG
    private int srcY = 0; //TODO RELOCATE PARALLAX BG

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(21f, 21f, 21f, 0, true);

        if (EntityManager.getInstance().getPlayerClient() == null) return;
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        tickGameLogic(delta);

        if (!mapRenderer.isReadyToRender()) return;
        camera.clampCamera(screenViewport, mapRenderer.getTiledMap());
        camera.update();

        //TODO RELOCATE PARALLAX BG
        if (mapRenderer.getGameMapNameFromServer().equals("floating_island")) {
            spriteBatch.begin();
            srcX += 2;
            srcY -= 3;
            if (srcX >= parallaxBackground.getWidth()) srcX = 0;
            if (srcY <= -parallaxBackground.getHeight()) srcY = 0;
            spriteBatch.draw(parallaxBackground, -parallaxBackground.getWidth(), -parallaxBackground.getHeight(), srcX, srcY, Gdx.graphics.getWidth() + parallaxBackground.getWidth() * 2, Gdx.graphics.getHeight() + parallaxBackground.getHeight() * 2);
            spriteBatch.end();
        }

        mapRenderer.renderBottomMapLayers(camera);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Valenguard.getInstance().getMouseManager().drawMoveNodes(spriteBatch);
        EntityManager.getInstance().drawEntityBodies(delta, spriteBatch);
        playerClient.getEntityAnimation().animate(delta, spriteBatch);
        EntityManager.getInstance().drawEntityNames();
        EntityManager.getInstance().drawDamageNumbers();
        playerClient.drawEntityName();
        playerClient.drawFloatingNumbers();
        spriteBatch.end();

        mapRenderer.renderOverheadMapLayers();
        Valenguard.getInstance().getMouseManager().drawMovingMouse(playerClient, spriteBatch);
        stageHandler.render(delta);
    }

    private void tickGameLogic(float delta) {
        Valenguard.getInstance().getClientMovementProcessor().processMovement(EntityManager.getInstance().getPlayerClient());
        Valenguard.getInstance().getClientPlayerMovementManager().processMoveNodes(EntityManager.getInstance().getPlayerClient(), delta);
        Valenguard.getInstance().getEntityMovementManager().tick(delta);
        Valenguard.getInstance().getEntityTracker().track();
    }

    @Override
    public void resize(int width, int height) {
        println(getClass(), "Invoked: resize(w: " + width + ", h: " + height + ")", false, PRINT_DEBUG);
        camera.setToOrtho(false, width, height);
        stageHandler.resize(width, height);
    }

    @Override
    public void pause() {
        gameFocused = false;
        println(getClass(), "Invoked: pause()", false, PRINT_DEBUG);
    }

    @Override
    public void resume() {
        /*
         * Here we set up a timer to return the game focus after a very short amount of time.
         * The reason we are doing this is to prevent a mouse click action from happening
         * right when the game window is resumed. This, prevents things like click-to-walk
         * movement when you click the game window to regan focus.
         */
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                println(getClass(), "Invoked: resume()", false, PRINT_DEBUG);
                gameFocused = true;
            }
        }, 0.1f);
    }

    @Override
    public void hide() {
        println(getClass(), "Invoked: hide()", false, PRINT_DEBUG);
    }

    @Override
    public void dispose() {
        println(getClass(), "Invoked: dispose()", false, PRINT_DEBUG);
        if (mapRenderer != null) mapRenderer.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
    }
}
