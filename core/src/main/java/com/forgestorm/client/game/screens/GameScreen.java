package com.forgestorm.client.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.GameTextures;
import com.forgestorm.client.game.audio.MusicManager;
import com.forgestorm.client.game.input.Keyboard;
import com.forgestorm.client.game.input.LongPressListener;
import com.forgestorm.client.game.input.Mouse;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.dev.spell.PixelFXTest;
import com.forgestorm.client.game.screens.ui.actors.dev.spell.SpellAnimationEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileAnimationEditor;
import com.forgestorm.client.game.world.WorldObject;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.ItemStackDrop;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.maps.*;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameFont;
import com.forgestorm.client.io.type.GameTexture;
import com.forgestorm.client.util.GraphicsUtils;
import com.forgestorm.client.util.PixmapUtil;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.Warp;
import com.forgestorm.shared.game.world.maps.WarpLocation;
import lombok.Getter;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Map;
import java.util.PriorityQueue;

import static com.forgestorm.client.util.Log.println;

@Getter
public class GameScreen implements Screen {

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;
    private final EntityManager entityManager;
    private final StageHandler stageHandler;
    private final FileManager fileManager;

    private AttachableCamera camera;
    private ScreenViewport screenViewport;
    private SpriteBatch spriteBatch;

    private boolean gameFocused = true;

    private final Keyboard keyboard;
    private final LongPressListener longPressListener;

    private BitmapFont font;

    // TODO: RELOCATE
    private Texture hpBase;
    private Texture hpArea;

    private Texture invalidTileLocationTexture;
    private Texture validTileLocationTexture;
    private Texture warpTileLocationTexture;

    private ShapeDrawer shapeDrawer;
    private Texture shapeDrawerTexture;

    @Getter
    private GameTextures entityShadow;

    private final PriorityQueue<WorldObject> ySortedWorldObjects = new PriorityQueue<>();

    public GameScreen(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.entityManager = clientMain.getEntityManager();
        this.stageHandler = clientMain.getStageHandler();
        this.fileManager = clientMain.getFileManager();
        this.keyboard = new Keyboard(clientMain);
        this.longPressListener = new LongPressListener(clientMain);
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        // Setup camera
        camera = new AttachableCamera(clientMain, ClientConstants.SCREEN_RESOLUTION, ClientConstants.ZOOM_DEFAULT);
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
        entityShadow = new GameTextures(fileManager.getTexture(GameTexture.SHADOW));

        // TODO: Change mouse cursor
//        fileManager.loadPixmap(GamePixmap.CURSOR_1);
//        Gdx.graphics.setCursor(Gdx.graphics.newCursor(fileManager.getPixmap(GamePixmap.CURSOR_1), 0, 0));

        // Setup input controls
        InputMultiplexer inputMultiplexer = clientMain.getInputMultiplexer();
        inputMultiplexer.addProcessor(stageHandler.getPreStageEvent());
        inputMultiplexer.addProcessor(stageHandler.getStage());
        inputMultiplexer.addProcessor(stageHandler.getPostStageEvent());
        inputMultiplexer.addProcessor(keyboard);
        if (Gdx.app.getType() == Application.ApplicationType.Android) inputMultiplexer.addProcessor(longPressListener);
        inputMultiplexer.addProcessor(new Mouse(clientMain));

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

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        shapeDrawerTexture = new Texture(pixmap); //remember to dispose of later
        pixmap.dispose();
        TextureRegion region = new TextureRegion(shapeDrawerTexture, 0, 0, 1, 1);
        shapeDrawer = new ShapeDrawer(spriteBatch, region);
    }

    @Override
    public void render(float delta) {
        if (clientMain.getWorldManager().getCurrentGameWorld() != null) {
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
        longPressListener.update();
        if (clientMain.getWorldManager().getCurrentGameWorld() == null) return;
        GraphicsUtils.clearScreen(clientMain.getWorldManager().getCurrentGameWorld().getBackgroundColor());

        if (clientMain.getEntityManager().getPlayerClient() == null) return;
        PlayerClient playerClient = entityManager.getPlayerClient();
        tickGameLogic(delta);

        camera.clampCamera(screenViewport, getGameMap());
        camera.update();

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        getGameMap().drawParallax(spriteBatch);

        // Loop the floors in reverse (bottom floor first, top floor last)
        Floors[] values = Floors.values();
        for (int i = values.length - 1; i >= 0; i--) {
            Floors floor = values[i];
            boolean isVisible = clientMain.getWorldBuilder().isFloorVisible(floor);

            if (isVisible) getGameMap().renderBottomLayers(spriteBatch, floor);

            entityManager.drawEntityShadows(spriteBatch, floor);
            entityManager.drawGroundEntities(spriteBatch);

            //////////////////////////////////////////////////
            //// -------- COLLECT WORLD OBJECTS -------- /////
            //////////////////////////////////////////////////

            if (isVisible) getGameMap().getSortableWorldObjects(ySortedWorldObjects, floor);
            entityManager.getSortableEntities(ySortedWorldObjects);
            ySortedWorldObjects.add(playerClient);

            //////////////////////////////////////////////////////
            //// -------- SORT & DRAW WORLD OBJECTS -------- /////
            //////////////////////////////////////////////////////
            while (!ySortedWorldObjects.isEmpty()) {
                WorldObject worldObject = ySortedWorldObjects.poll();

                if (worldObject instanceof MovingEntity) {
                    MovingEntity movingEntity = (MovingEntity) worldObject;
                    if (movingEntity.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;
                    movingEntity.getEntityAnimation().animate(delta, spriteBatch);
                } else if (worldObject instanceof ItemStackDrop) {
                    ItemStackDrop itemStackDrop = (ItemStackDrop) worldObject;
                    if (itemStackDrop.getCurrentMapLocation().getZ() != floor.getWorldZ()) continue;

                    TextureRegion textureRegion = clientMain.getItemStackManager().getItemStackTextureRegion(itemStackDrop.getAppearance().getSingleBodyTexture());
                    spriteBatch.draw(textureRegion,
                            itemStackDrop.getDrawX() + 4,
                            itemStackDrop.getDrawY(),
                            8,
                            8);

                } else if (worldObject instanceof Tile) {

                    Tile tile = (Tile) worldObject;

                    // TODO CHANGE LOCATION!
                    Location playerLocation = entityManager.getPlayerClient().getCurrentMapLocation();
                    if (!renderRegion(playerLocation, tile)) continue;

                    TileImage tileImage = tile.getTileImage();
                    if (tileImage == null) continue;

                    TextureRegion textureRegion = tileImage.getTextureRegion();

                    final float TILE_SIZE_FIX = 0.005F;
                    spriteBatch.draw(textureRegion,
                            tile.getDrawX(),
                            tile.getDrawY(),
                            textureRegion.getRegionWidth() + TILE_SIZE_FIX,
                            textureRegion.getRegionHeight() + TILE_SIZE_FIX);
                }
            }
            //////////////////////////////////////////////////////////
            //// -------- END SORT & DRAW WORLD OBJECTS -------- /////
            //////////////////////////////////////////////////////////

            if (isVisible) getGameMap().renderDecorationLayer(spriteBatch, floor);

            // Draw damage animations
            clientMain.getAbilityManager().drawAnimation(delta, spriteBatch);

            // Render warp texture
            if (stageHandler.getWarpEditor() != null && stageHandler.getWarpEditor().isVisible()) {
                for (WorldChunk worldChunk : clientMain.getWorldManager().getCurrentGameWorld().getWorldChunkDrawMap().values()) {
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
            entityManager.drawEntityNames(floor);
            playerClient.drawEntityName();

            // Draw HP Bar
            entityManager.drawHealthBar(floor);
            playerClient.drawEntityHpBar();

            // Draw damage indicators
            entityManager.drawDamageNumbers(floor);
            playerClient.drawFloatingNumbers();

            // Draw Level up messages
            playerClient.drawLevelUpMessage();

            // Render overhead layer here
            if (isVisible) getGameMap().renderOverheadLayer(spriteBatch, floor);
        }

        // Draw mouse
        MouseManager mouseManager = clientMain.getMouseManager();
        mouseManager.drawMoveNodes(spriteBatch);
        mouseManager.drawMouseHoverIcon(spriteBatch, validTileLocationTexture, invalidTileLocationTexture);

        // Draw World Builder
        clientMain.getWorldBuilder().drawMouse(spriteBatch);

        PixelFXTest pixelFXTest = clientMain.getStageHandler().getPixelFXTest();
        if (pixelFXTest != null) pixelFXTest.render(delta, spriteBatch);

        TileAnimationEditor tileAnimationEditor = clientMain.getStageHandler().getTileAnimationEditor();
        if (tileAnimationEditor != null) tileAnimationEditor.render();

        SpellAnimationEditor spellAnimationEditor = clientMain.getStageHandler().getSpellAnimationEditor();
        if (spellAnimationEditor != null)
            spellAnimationEditor.getAnimationEffect().renderAllAnimationPartDataTables(delta);

        clientMain.getMouseManager().drawMovingMouse(playerClient, spriteBatch);

        // TODO: TEST DRAWLING REGION
        clientMain.getRegionManager().editRegion(shapeDrawer);

        spriteBatch.end();
    }

    private boolean renderRegion(Location playerLocation, Tile tile) {
        // Region management
        RegionManager regionManager = clientMain.getRegionManager();
        Region region = regionManager.getRegionToEdit();

        if (region == null) return true;

        boolean playerIntersect = region.doesIntersect(playerLocation.getX(), playerLocation.getY());
        boolean tileIntersect = region.doesIntersect(tile.getWorldX(), tile.getWorldY());

        return !playerIntersect || !tileIntersect || tile.getWorldZ() <= playerLocation.getZ();
    }

    private GameWorld getGameMap() {
        return clientMain.getWorldManager().getCurrentGameWorld();
    }

    private void tickGameLogic(float delta) {
        clientMain.getClientMovementProcessor().processMovement(entityManager.getPlayerClient());
        clientMain.getClientPlayerMovementManager().processMoveNodes(entityManager.getPlayerClient());
        clientMain.getEntityMovementManager().tick(delta);
        clientMain.getEntityTracker().followTick();
        clientMain.getEntityTracker().walkToTick();
        clientMain.getAbilityManager().updateCooldowns();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stageHandler.resize(width, height);
    }

    @Override
    public void pause() {
        gameFocused = false;
        clientMain.getAudioManager().getMusicManager().pauseMusic();
    }

    @Override
    public void resume() {
        // Resume game music, if applicable
        final MusicManager musicManager = clientMain.getAudioManager().getMusicManager();
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
        if (shapeDrawerTexture != null) shapeDrawerTexture.dispose();
        entityShadow.dispose();
        stageHandler.dispose();
    }
}
