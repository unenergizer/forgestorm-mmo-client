package com.forgestorm.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.movement.ClientMovementProcessor;
import com.forgestorm.client.game.movement.InputData;
import com.forgestorm.client.game.movement.MoveUtil;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.world.WorldBuilder;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.ItemStackDrop;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.entities.StationaryEntity;
import com.forgestorm.client.game.world.maps.CursorDrawType;
import com.forgestorm.client.game.world.maps.GameMap;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.MapUtil;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.ClickActionPacketOut;
import com.forgestorm.client.util.FadeOut;
import com.forgestorm.client.util.MoveNode;
import com.forgestorm.client.util.PathFinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class MouseManager {

    private static final boolean PRINT_DEBUG = false;

    public static final int NUM_TICKS_TO_FADE_MOUSE = 60;

    private final PathFinding pathFinding = new PathFinding();

    private final Vector3 clickLocation = new Vector3();
    @Getter
    private short leftClickTileX, leftClickTileY;
    @Getter
    private short rightClickTileX, rightClickTileY;
    @Getter
    private short mouseTileX, mouseTileY;
    private float mouseWorldX, mouseWorldY;

    @Setter
    private boolean invalidate = true;

    private Timer.Task waitForMouseFadeTask;

    @Getter
    private FadeOut fadeOut = new FadeOut();

    @Getter
    @Setter
    private boolean highlightHoverTile = false;

    void mouseMove(final int screenX, final int screenY) {
        final Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = (short) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.mouseTileY = (short) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);
        this.mouseWorldX = tiledMapCoordinates.x;
        this.mouseWorldY = tiledMapCoordinates.y;

        if (waitForMouseFadeTask != null) {
            waitForMouseFadeTask.cancel();
        }

        fadeOut.cancelFade();

        waitForMouseFadeTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!fadeOut.isFading()) {
                    fadeOut.startFade(NUM_TICKS_TO_FADE_MOUSE);
                }
            }
        }, 2);
    }

    void mouseClick(final int screenX, final int screenY, final int button) {

        if (invalidate) return;

        if (button == Input.Buttons.LEFT) left(screenX, screenY);
        else if (button == Input.Buttons.MIDDLE) middle(screenX, screenY);
        else if (button == Input.Buttons.RIGHT) right(screenX, screenY);
        else if (button == Input.Buttons.FORWARD) forward(screenX, screenY);
        else if (button == Input.Buttons.BACK) back(screenX, screenY);
    }

    private Vector3 cameraXYtoTiledMapXY(final int screenX, final int screenY) {
        return ClientMain.gameScreen.getCamera().unproject(clickLocation.set(screenX, screenY, 0));
    }

    private boolean entityClickTest(float drawX, float drawY) {
        if (mouseWorldX >= drawX && mouseWorldX < drawX + 16) {
            return mouseWorldY >= drawY && mouseWorldY < drawY + 16;
        }
        return false;
    }

    private void left(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.leftClickTileX = (short) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.leftClickTileY = (short) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Location clientLocation = playerClient.getFutureMapLocation();

        WorldBuilder worldBuilder = ActorUtil.getStageHandler().getWorldBuilder();
        if (worldBuilder.isVisible()) {
            TiledMap tiledMap = ClientMain.gameScreen.getMapRenderer().getTiledMap();
            MapLayers layers = tiledMap.getLayers();
            TiledMapTileLayer layer = (TiledMapTileLayer) layers.get(worldBuilder.getActiveDrawLayer());
            TiledMapTileLayer.Cell cell = layer.getCell(mouseTileX, mouseTileY);

            if (cell == null) {
                println(getClass(), "Cell was null, creating a new one!");
                cell = new TiledMapTileLayer.Cell();
            } else {
                println(getClass(), "Cell found!");
            }

            println(getClass(), "TiledMapLayer: " + layer.getName() + ", Is Null: " + (layer == null) + ", Width: " + layer.getWidth() + ", Height: " + layer.getHeight());
            println(getClass(), "Cell Null: " + (cell == null) + ", Cell X: " + mouseTileX + ", Cell Y: " + mouseTileY);
            TextureRegion textureRegion = worldBuilder.getWorldBuilderTile();

            if (cell.getTile() == null) {
                StaticTiledMapTile tiledMapTile = new StaticTiledMapTile(textureRegion);
                cell.setTile(tiledMapTile);
                layer.setCell(mouseTileX, mouseTileY, cell);
            } else {
                cell.getTile().setTextureRegion(textureRegion);
            }

        }

        // If setting the spawn of an entity, prevent the mouse from making the player walk.
        EntityEditor entityEditor = ActorUtil.getStageHandler().getEntityEditor();
        if (entityEditor != null) {
            if (entityEditor.getNpcTab().isSelectSpawnActivated()) return;
            if (entityEditor.getMonsterTab().isSelectSpawnActivated()) return;
        }

        // Left Click target AiEntities
        for (MovingEntity movingEntity : EntityManager.getInstance().getAiEntityList().values()) {
            if (entityClickTest(movingEntity.getDrawX(), movingEntity.getDrawY())) {
                ClientMain.getInstance().getEntityTracker().startTracking(movingEntity);
                EntityManager.getInstance().getPlayerClient().setTargetEntity(movingEntity);
                return;
            }
        }

        // Left Click Target Player entities
        for (MovingEntity movingEntity : EntityManager.getInstance().getPlayerEntityList().values()) {
            if (entityClickTest(movingEntity.getDrawX(), movingEntity.getDrawY())) {
                ClientMain.getInstance().getEntityTracker().startTracking(movingEntity);
                EntityManager.getInstance().getPlayerClient().setTargetEntity(movingEntity);
                return;
            }
        }

        // Skill nodes like Mining and Fishing etc
        Queue<MoveNode> moveNodes = null;
        for (StationaryEntity stationaryEntity : EntityManager.getInstance().getStationaryEntityList().values()) {
            if (entityClickTest(stationaryEntity.getDrawX(), stationaryEntity.getDrawY())) {
                Location location = stationaryEntity.getCurrentMapLocation();

                if (clientLocation.isWithinDistance(location, (short) 1)) {
                    // The player is requesting to interact with the entity.
                    if (!MoveUtil.isEntityMoving(playerClient)) {
                        new ClickActionPacketOut(new ClickAction(ClickAction.LEFT, stationaryEntity)).sendPacket();
                    }
                } else {
                    // New Entity click so lets cancelTracking entityTracker
                    ClientMain.getInstance().getEntityTracker().cancelTracking();

                    // Top right quad
                    Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), leftClickTileX, leftClickTileY, clientLocation.getMapName(), true);
                    if (testMoveNodes == null) break;
                    moveNodes = new LinkedList<MoveNode>();
                    for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                        moveNodes.add(testMoveNodes.remove());
                    }

                }
                break;
            }
        }

        // Picking up ItemStacks from the ground
        for (ItemStackDrop itemStackDrop : EntityManager.getInstance().getItemStackDropList().values()) {
            if (entityClickTest(itemStackDrop.getDrawX(), itemStackDrop.getDrawY())) {
                Location location = itemStackDrop.getCurrentMapLocation();

                if (clientLocation.isWithinDistance(location, (short) 1)) {
                    // The player is requesting to interact with the entity.
                    if (!MoveUtil.isEntityMoving(playerClient)) {
                        println(getClass(), "ItemStack clicked! ID: " + itemStackDrop.getServerEntityID());
                        new ClickActionPacketOut(new ClickAction(ClickAction.LEFT, itemStackDrop)).sendPacket();
                    }
                } else {
                    // New Entity click so lets cancelTracking entityTracker
                    ClientMain.getInstance().getEntityTracker().cancelTracking();

                    // Top right quad
                    Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), leftClickTileX, leftClickTileY, clientLocation.getMapName(), true);
                    if (testMoveNodes == null) break;
                    moveNodes = new LinkedList<MoveNode>();
                    for (int i = testMoveNodes.size() - 1; i > 0; i--) {
                        moveNodes.add(testMoveNodes.remove());
                    }

                    println(getClass(), "Generated path to itemstack");
                }
                break;
            }
        }

        // Click to walk path finding
        if (moveNodes == null) {
            // New Entity click so lets cancelTracking entityTracker
            ClientMain.getInstance().getEntityTracker().cancelTracking();
            moveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), leftClickTileX, leftClickTileY, clientLocation.getMapName(), false);
        }

        if (moveNodes == null) return;

        ClientMain.getInstance().getClientMovementProcessor().postProcessMovement(
                new InputData(ClientMovementProcessor.MovementInput.MOUSE, moveNodes, null));
    }

    private void middle(final int screenX, final int screenY) {
        println(getClass(), "Middle Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private void right(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.rightClickTileX = (short) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.rightClickTileY = (short) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        /*
         * Build right click menu!
         */
        final List<Entity> entityList = new ArrayList<Entity>();

        for (Player player : EntityManager.getInstance().getPlayerEntityList().values()) {
            if (entityClickTest(player.getDrawX(), player.getDrawY())) {
                entityList.add(player);
            }
        }

        for (MovingEntity movingEntity : EntityManager.getInstance().getAiEntityList().values()) {
            if (entityClickTest(movingEntity.getDrawX(), movingEntity.getDrawY())) {
                entityList.add(movingEntity);
            }
        }

        for (ItemStackDrop itemStackDrop : EntityManager.getInstance().getItemStackDropList().values()) {
            if (entityClickTest(itemStackDrop.getDrawX(), itemStackDrop.getDrawY())) {
                entityList.add(itemStackDrop);
            }
        }

        // Send list of entities to the EntityDropDownMenu!
        if (!entityList.isEmpty()) {
            if (ActorUtil.getStageHandler().getTradeWindow().isVisible()) return;
            ActorUtil.getStageHandler().getEntityDropDownMenu().toggleMenu(entityList, screenX, ClientMain.gameScreen.getCamera().viewportHeight - screenY);
        }

        /*
         * Right clicked stationary node...
         */

        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        Location clientLocation = playerClient.getCurrentMapLocation();

        for (StationaryEntity stationaryEntity : EntityManager.getInstance().getStationaryEntityList().values()) {
            if (entityClickTest(stationaryEntity.getDrawX(), stationaryEntity.getDrawY())) {
                Location location = stationaryEntity.getCurrentMapLocation();

                if (!MoveUtil.isEntityMoving(playerClient)) {
                    if (clientLocation.isWithinDistance(location, (short) 1)) {
                        // The player is requesting to interact with the entity.
                        new ClickActionPacketOut(new ClickAction(ClickAction.RIGHT, stationaryEntity)).sendPacket();
                    }
                }
            }
        }
    }

    private void forward(final int screenX, final int screenY) {
        println(getClass(), "Forward Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private void back(final int screenX, final int screenY) {
        println(getClass(), "Back Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private CursorDrawType lastCursorDrawType = CursorDrawType.NO_DRAWABLE;
    private TextureRegionDrawable cursorDrawable;

    public void drawMovingMouse(PlayerClient playerClient, SpriteBatch spriteBatch) {
        GameMap gameMap = playerClient.getGameMap();
        if (MapUtil.isOutOfBounds(gameMap, mouseTileX, mouseTileY)) return;
        CursorDrawType cursorDrawType = gameMap.getMapTiles()[mouseTileX][mouseTileY].getCursorDrawType();

        if (cursorDrawType != lastCursorDrawType && cursorDrawType != CursorDrawType.NO_DRAWABLE) {
            lastCursorDrawType = cursorDrawType;
            cursorDrawable = new ImageBuilder(GameAtlas.CURSOR, cursorDrawType.getDrawableRegion(), cursorDrawType.getSize()).buildTextureRegionDrawable();
        }

        if (cursorDrawType == CursorDrawType.NO_DRAWABLE) return;
        fadeOut.draw(spriteBatch,
                cursorDrawable,
                mouseWorldX - cursorDrawType.getSize() / 2f,
                mouseWorldY - cursorDrawType.getSize() / 2f,
                cursorDrawType.getSize(),
                cursorDrawType.getSize());
    }

    public void drawMoveNodes(SpriteBatch spriteBatch) {
        if (ClientMain.getInstance().getClientMovementProcessor().getCurrentMovementInput() == ClientMovementProcessor.MovementInput.MOUSE) {
            Queue<MoveNode> remainingMoveNodes = ClientMain.getInstance().getClientPlayerMovementManager().getMovements();
            for (MoveNode moveNode : remainingMoveNodes) {
                spriteBatch.draw(new ImageBuilder(GameAtlas.CURSOR, "path_find").buildTextureRegionDrawable().getRegion(), moveNode.getWorldX() * ClientConstants.TILE_SIZE, moveNode.getWorldY() * ClientConstants.TILE_SIZE);
            }
        }
    }

    public void invalidateMouse() {
        invalidate = true;
    }
}
