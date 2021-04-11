package com.forgestorm.client.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.GameTextures;
import com.forgestorm.client.game.audio.MusicManager;
import com.forgestorm.client.game.input.Keyboard;
import com.forgestorm.client.game.input.Mouse;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.dev.PixelFXTest;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Warp;
import com.forgestorm.client.game.world.maps.WarpLocation;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameFont;
import com.forgestorm.client.io.type.GameTexture;
import com.forgestorm.client.util.GraphicsUtils;
import com.forgestorm.client.util.PixmapUtil;

import java.util.Map;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

@Getter
public class GameScreen implements Screen {

    private static final boolean PRINT_DEBUG = false;

    private final StageHandler stageHandler;
    private final FileManager fileManager = ClientMain.getInstance().getFileManager();

    private AttachableCamera camera;
    private ScreenViewport screenViewport;
    private SpriteBatch spriteBatch;

    private boolean gameFocused = true;

    private final Keyboard keyboard = new Keyboard();

    private BitmapFont font;

    // TODO: RELOCATE
    private Texture hpBase;
    private Texture hpArea;

    private Texture invalidTileLocationTexture;
    private Texture validTileLocationTexture;
    private Texture warpTileLocationTexture;

    public GameScreen(StageHandler stageHandler) {
        this.stageHandler = stageHandler;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        // Setup camera
        camera = new AttachableCamera(ClientConstants.SCREEN_RESOLUTION, ClientConstants.ZOOM_DEFAULT);
        screenViewport = new ScreenViewport();
        stageHandler.setViewport(screenViewport);

        // Load Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(GameFont.BITCELL.getFilePath()));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.kerning = false;
        parameter.packer = null;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 1.5f;
        parameter.gamma = 1;
        parameter.mono = true;
        parameter.size = 15;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        generator.dispose();

        // Setting global textures
        GameTextures.entityShadow = fileManager.getTexture(GameTexture.SHADOW);

        // TODO: Change mouse cursor
//        fileManager.loadPixmap(GamePixmap.CURSOR_1);
//        Gdx.graphics.setCursor(Gdx.graphics.newCursor(fileManager.getPixmap(GamePixmap.CURSOR_1), 0, 0));

        // Setup input controls
        InputMultiplexer inputMultiplexer = ClientMain.getInstance().getInputMultiplexer();
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

        Pixmap warpTextureHighlight = PixmapUtil.createProceduralPixmap(16, 16, Color.PURPLE);
        warpTileLocationTexture = new Texture(warpTextureHighlight);
        warpTextureHighlight.dispose();
    }

    @Override
    public void render(float delta) {
        if (ClientMain.getInstance().getWorldManager().getCurrentGameWorld() != null) {
            renderGame(delta);
        } else {
            renderAccountInformation();
        }

        stageHandler.render(delta);
    }

    private void renderAccountInformation() {
        GraphicsUtils.clearScreen(Color.BLACK);
        Texture texture = fileManager.getTexture(GameTexture.LOGIN_BACKGROUND);

        if (texture == null) return;
        spriteBatch.begin();
        spriteBatch.draw(texture, 0, 0, texture.getWidth(), texture.getHeight());
        spriteBatch.end();
    }

    private void renderGame(float delta) {
        if (ClientMain.getInstance().getWorldManager().getCurrentGameWorld() == null) return;
        GraphicsUtils.clearScreen(ClientMain.getInstance().getWorldManager().getCurrentGameWorld().getBackgroundColor());

        if (EntityManager.getInstance().getPlayerClient() == null) return;
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        tickGameLogic(delta);

        camera.clampCamera(screenViewport, getGameMap());
        camera.update();

        spriteBatch.begin();

        getGameMap().drawParallax(spriteBatch);

        getGameMap().renderBottomLayers(spriteBatch);

        spriteBatch.setProjectionMatrix(camera.combined);

        EntityManager.getInstance().drawEntityShadows(spriteBatch);

        getGameMap().renderDecorationLayer(spriteBatch);

        // Drawing all entities. Ground items, moving entities, ect...
        EntityManager.getInstance().drawEntities(delta, spriteBatch, playerClient);

        // Draw damage animations
        ClientMain.getInstance().getAbilityManager().drawAnimation(delta, spriteBatch);

        // Render overhead layer here
        getGameMap().renderOverheadLayer(spriteBatch);

        // Render warp texture
        if (stageHandler.getWarpEditor() != null && stageHandler.getWarpEditor().isVisible()) {
            for (WorldChunk worldChunk : ClientMain.getInstance().getWorldManager().getCurrentGameWorld().getWorldChunkMap().values()) {
                for (Map.Entry<WarpLocation, Warp> entry : worldChunk.getTileWarps().entrySet()) {
                    float fromX = entry.getKey().getFromX() * ClientConstants.TILE_SIZE * ClientConstants.CHUNK_SIZE;
                    float fromY = entry.getKey().getFromY() * ClientConstants.TILE_SIZE * ClientConstants.CHUNK_SIZE;
                    float toX = entry.getValue().getWarpDestination().getX() * ClientConstants.TILE_SIZE;
                    float toY = entry.getValue().getWarpDestination().getY() * ClientConstants.TILE_SIZE;
                    spriteBatch.draw(warpTileLocationTexture, fromX, fromY);
                    spriteBatch.draw(warpTileLocationTexture, toX, toY);
                }
            }
        }

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
        MouseManager mouseManager = ClientMain.getInstance().getMouseManager();
        mouseManager.drawMoveNodes(spriteBatch);
        mouseManager.drawMouseHoverIcon(spriteBatch, validTileLocationTexture, invalidTileLocationTexture);

        // Draw World Builder
        ClientMain.getInstance().getWorldBuilder().drawMouse(spriteBatch);

        PixelFXTest pixelFXTest = ClientMain.getInstance().getStageHandler().getPixelFXTest();
        if (pixelFXTest != null) pixelFXTest.render(delta, spriteBatch);

        ClientMain.getInstance().getMouseManager().drawMovingMouse(playerClient, spriteBatch);
        spriteBatch.end();
    }

    private GameWorld getGameMap() {
        return ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
    }

    private void tickGameLogic(float delta) {
        ClientMain.getInstance().getClientMovementProcessor().processMovement(EntityManager.getInstance().getPlayerClient());
        ClientMain.getInstance().getClientPlayerMovementManager().processMoveNodes(EntityManager.getInstance().getPlayerClient());
        ClientMain.getInstance().getEntityMovementManager().tick(delta);
        ClientMain.getInstance().getEntityTracker().followTick();
        ClientMain.getInstance().getEntityTracker().walkToTick();
        ClientMain.getInstance().getAbilityManager().updateCooldowns();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stageHandler.resize(width, height);
    }

    @Override
    public void pause() {
        gameFocused = false;
        ClientMain.getInstance().getAudioManager().getMusicManager().pauseMusic();
    }

    @Override
    public void resume() {
        // Resume game music, if applicable
        final MusicManager musicManager = ClientMain.getInstance().getAudioManager().getMusicManager();
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
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
        if (font != null) font.dispose();
        if (hpBase != null) hpBase.dispose();
        if (hpArea != null) hpArea.dispose();
        if (invalidTileLocationTexture != null) invalidTileLocationTexture.dispose();
        if (validTileLocationTexture != null) validTileLocationTexture.dispose();
        if (warpTileLocationTexture != null) warpTileLocationTexture.dispose();
        GameTextures.dispose();
        stageHandler.dispose();
    }
}
