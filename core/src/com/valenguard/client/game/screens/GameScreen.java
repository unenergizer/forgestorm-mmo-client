package com.valenguard.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.audio.MusicManager;
import com.valenguard.client.game.input.Keyboard;
import com.valenguard.client.game.input.Mouse;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.dev.world.WorldBuilder;
import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.entities.Player;
import com.valenguard.client.game.world.entities.PlayerClient;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.game.world.maps.MapRenderer;
import com.valenguard.client.game.world.maps.MapUtil;
import com.valenguard.client.io.FileManager;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.io.type.GameFont;
import com.valenguard.client.io.type.GameTexture;
import com.valenguard.client.util.GraphicsUtils;
import com.valenguard.client.util.PixmapUtil;

import java.util.Map;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

@Getter
public class GameScreen implements Screen {

    private static final boolean PRINT_DEBUG = false;

    private final StageHandler stageHandler;
    private final FileManager fileManager = Valenguard.getInstance().getFileManager();

    private MapRenderer mapRenderer;
    private AttachableCamera camera;
    private ScreenViewport screenViewport;
    private SpriteBatch spriteBatch;

    private boolean gameFocused = true;

    private Keyboard keyboard = new Keyboard();

    // TODO: RELOCATE
    private Texture parallaxBackground;
    private int srcX = 0; //TODO RELOCATE PARALLAX BG
    private int srcY = 0; //TODO RELOCATE PARALLAX BG

    private BitmapFont font;

    // TODO: RELOCATE
    private Texture hpBase;
    private Texture hpArea;

    private Texture invalidTileLocationTexture;
    private Texture validTileLocationTexture;

    @Getter
    private Texture shadow;
    private int distance;
    private Color darkGray;
    private Color red;

    public GameScreen(StageHandler stageHandler) {
        println(getClass(), "Invoked constructor", false, PRINT_DEBUG);
        this.stageHandler = stageHandler;
    }

    @Override
    public void show() {
        println(getClass(), "Invoked: show()", false, PRINT_DEBUG);
        spriteBatch = new SpriteBatch();
        mapRenderer = new MapRenderer(spriteBatch);

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_RESOLUTION, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();
        stageHandler.setViewport(screenViewport);

        // Load assets
        GameFont gameFont = GameFont.PIXEL;
        fileManager.loadFont(gameFont);
        font = fileManager.getFont(gameFont);
        font.setUseIntegerPositions(false);

        fileManager.loadAtlas(GameAtlas.CURSOR);
        fileManager.loadTexture(GameTexture.LOGIN_BACKGROUND);
        fileManager.loadAtlas(GameAtlas.ENTITY_CHARACTER);
        fileManager.loadAtlas(GameAtlas.ENTITY_MONSTER);
        fileManager.loadAtlas(GameAtlas.SKILL_NODES);
        fileManager.loadAtlas(GameAtlas.TILES);

        // Entity Shadows
        fileManager.loadTexture(GameTexture.SHADOW);
        shadow = fileManager.getTexture(GameTexture.SHADOW);

        distance = -3;
        darkGray = Color.DARK_GRAY;
        darkGray.a = .7f;
        red = Color.RED;
        red.a = .7f;

        // Parallax Background
        fileManager.loadTexture(GameTexture.PARALLAX_BACKGROUND);
        parallaxBackground = fileManager.getTexture(GameTexture.PARALLAX_BACKGROUND);
        parallaxBackground.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // TODO: Change mouse cursor
//        fileManager.loadPixmap(GamePixmap.CURSOR_1);
//        Gdx.graphics.setCursor(Gdx.graphics.newCursor(fileManager.getPixmap(GamePixmap.CURSOR_1), 0, 0));

        // Setup input controls
        InputMultiplexer inputMultiplexer = Valenguard.getInstance().getInputMultiplexer();
        inputMultiplexer.addProcessor(stageHandler.getPreStageEvent());
        inputMultiplexer.addProcessor(stageHandler.getStage());
        inputMultiplexer.addProcessor(stageHandler.getPostStageEvent());
        inputMultiplexer.addProcessor(keyboard);
        inputMultiplexer.addProcessor(new Mouse());

        // Create HealthBar textures
        final int width = 1;
        final int height = 1;
        Pixmap hpBasePixmap = PixmapUtil.createProceduralPixmap(width, height, Color.RED);
        hpBase = new Texture(hpBasePixmap);
        hpBasePixmap.dispose();

        Pixmap hpAreaPixmap = PixmapUtil.createProceduralPixmap(width, height, Color.GREEN);
        hpArea = new Texture(hpAreaPixmap);
        hpAreaPixmap.dispose();

        // Tile highlighting
        Pixmap invalidTextureHighlight = PixmapUtil.createProceduralPixmap(16, 16, Color.RED);
        invalidTileLocationTexture = new Texture(invalidTextureHighlight);
        invalidTextureHighlight.dispose();

        Pixmap validTextureHighlight = PixmapUtil.createProceduralPixmap(16, 16, Color.GREEN);
        validTileLocationTexture = new Texture(validTextureHighlight);
        validTextureHighlight.dispose();

        WorldBuilder worldBuilder = stageHandler.getWorldBuilder();
        if (worldBuilder.getWorldBuilderTile() == null) {
            TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
            worldBuilder.setWorldBuilderTile(textureAtlas.findRegion("decoration"));
        }
    }

    @Override
    public void render(float delta) {
        GraphicsUtils.clearScreen(Valenguard.getInstance().getMapManager().getBackgroundColor());

        // Render
        if (mapRenderer.isReadyToRender()) {
            renderGame(delta);
        } else {
            renderAccountInformation();
        }

        stageHandler.render(delta);
    }

    private void renderAccountInformation() {
        Texture texture = fileManager.getTexture(GameTexture.LOGIN_BACKGROUND);

        spriteBatch.begin();
        spriteBatch.draw(texture, 0, 0, texture.getWidth(), texture.getHeight());
        spriteBatch.end();
    }

    private void renderGame(float delta) {
        if (!mapRenderer.isReadyToRender()) return;
        if (EntityManager.getInstance().getPlayerClient() == null) return;
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        tickGameLogic(delta);

        camera.clampCamera(screenViewport, mapRenderer.getTiledMap());
        camera.update();
        spriteBatch.begin();

        //TODO RELOCATE PARALLAX BG
        if (mapRenderer.getGameMapNameFromServer().equals("floating_island")) {
            srcX += 2;
            srcY -= 3;
            if (srcX >= parallaxBackground.getWidth()) srcX = 0;
            if (srcY <= -parallaxBackground.getHeight()) srcY = 0;
            spriteBatch.draw(parallaxBackground, -parallaxBackground.getWidth(), -parallaxBackground.getHeight(), srcX, srcY, Gdx.graphics.getWidth() + parallaxBackground.getWidth() * 2, Gdx.graphics.getHeight() + parallaxBackground.getHeight() * 2);
        }

        mapRenderer.renderBottomMapLayers(camera);

        // Draw Screen Effects
        spriteBatch.end();
        Valenguard.getInstance().getEffectManager().drawScreenEffect();
        spriteBatch.begin();

        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw Shadow and Shadow color
        spriteBatch.setColor(darkGray);
        Map<Short, AiEntity> aiEntityMap = EntityManager.getInstance().getAiEntityList();
        for (AiEntity aiEntity : aiEntityMap.values()) {
            spriteBatch.draw(shadow, aiEntity.getDrawX(), aiEntity.getDrawY() + distance);
        }
        Map<Short, Player> playerEntityList = EntityManager.getInstance().getPlayerEntityList();
        for (Player player : playerEntityList.values()) {
            spriteBatch.draw(shadow, player.getDrawX(), player.getDrawY() + distance);
        }
        MovingEntity movingEntity = playerClient.getTargetEntity();
        if (movingEntity != null) {
            spriteBatch.setColor(red);
            spriteBatch.draw(shadow, movingEntity.getDrawX(), movingEntity.getDrawY() + distance);
        }
        Player player = EntityManager.getInstance().getPlayerClient();
        spriteBatch.setColor(darkGray);
        spriteBatch.draw(shadow, player.getDrawX(), player.getDrawY() + distance);
        spriteBatch.setColor(Color.WHITE); // RESET COLOR

        // Animate
        EntityManager.getInstance().drawEntityBodies(delta, spriteBatch, playerClient);

        mapRenderer.renderOverheadMapLayers();

        // Draw Names
        EntityManager.getInstance().drawEntityNames();
        playerClient.drawEntityName();

        // Draw HP Bar
        EntityManager.getInstance().drawHealthBar();
        playerClient.drawEntityHpBar();

        // Draw damage indicators
        EntityManager.getInstance().drawDamageNumbers();
        playerClient.drawFloatingNumbers();

        // Draw Level up messages
        playerClient.drawLevelUpMessage();

        // Draw mouse
        MouseManager mouseManager = Valenguard.getInstance().getMouseManager();
        mouseManager.drawMoveNodes(spriteBatch);
        if (mouseManager.isHighlightHoverTile()) {
            int x = mouseManager.getMouseTileX() * 16;
            int y = mouseManager.getMouseTileY() * 16;
            Location clientLocation = playerClient.getCurrentMapLocation();
            if (MapUtil.isTraversable(clientLocation.getMapData(), mouseManager.getMouseTileX(), mouseManager.getMouseTileY())) {
                spriteBatch.draw(validTileLocationTexture, x, y, 16, 16);
            } else {
                spriteBatch.draw(invalidTileLocationTexture, x, y, 16, 16);
            }
        }

        // Draw World Builder
        if (stageHandler.getWorldBuilder().isVisible() && stageHandler.getWorldBuilder().getWorldBuilderTile() != null) {
            int x = mouseManager.getMouseTileX() * 16;
            int y = mouseManager.getMouseTileY() * 16;
            spriteBatch.draw(stageHandler.getWorldBuilder().getWorldBuilderTile(), x, y, 16, 16);
        }

        Valenguard.getInstance().getMouseManager().drawMovingMouse(playerClient, spriteBatch);
        spriteBatch.end();
    }

    private void tickGameLogic(float delta) {
        Valenguard.getInstance().getClientMovementProcessor().processMovement(EntityManager.getInstance().getPlayerClient());
        Valenguard.getInstance().getClientPlayerMovementManager().processMoveNodes(EntityManager.getInstance().getPlayerClient(), delta);
        Valenguard.getInstance().getEntityMovementManager().tick(delta);
        Valenguard.getInstance().getEntityTracker().track();
        Valenguard.getInstance().getAbilityManager().updateCooldowns();
        Valenguard.getInstance().getEffectManager().tickScreenEffect(delta);
    }

    @Override
    public void resize(int width, int height) {
        println(getClass(), "Invoked: resize(w: " + width + ", h: " + height + ")", false, PRINT_DEBUG);
        camera.setToOrtho(false, width, height);
        stageHandler.resize(width, height);
    }

    @Override
    public void pause() {
        println(getClass(), "Invoked: pause()", false, PRINT_DEBUG);
        gameFocused = false;
        Valenguard.getInstance().getAudioManager().getMusicManager().pauseMusic();
    }

    @Override
    public void resume() {
        // Resume game music, if applicable
        final MusicManager musicManager = Valenguard.getInstance().getAudioManager().getMusicManager();
        if (musicManager.isMusicPaused()) musicManager.resumeMusic();

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
        mapRenderer.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        if (font != null) font.dispose();
        if (parallaxBackground != null) parallaxBackground.dispose();
        if (hpBase != null) hpBase.dispose();
        if (hpArea != null) hpArea.dispose();
        if (invalidTileLocationTexture != null) invalidTileLocationTexture.dispose();
        if (validTileLocationTexture != null) validTileLocationTexture.dispose();
        if (shadow != null) shadow.dispose();
        stageHandler.dispose();
    }
}
