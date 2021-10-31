package com.forgestorm.client.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.movement.ClientMovementProcessor;
import com.forgestorm.client.game.movement.MoveUtil;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileBuildMenu;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.client.game.world.entities.EntityInteract;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.ItemStackDrop;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.client.game.world.entities.NPC;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.game.world.entities.StationaryEntity;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.RegionManager;
import com.forgestorm.client.game.world.maps.WorldUtil;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.network.game.packet.out.ClickActionPacketOut;
import com.forgestorm.client.util.FadeOut;
import com.forgestorm.client.util.MoveNode;
import com.forgestorm.shared.game.world.maps.CursorDrawType;
import com.forgestorm.shared.io.type.GameAtlas;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

import static com.forgestorm.client.util.Log.println;

public class MouseManager {

    private static final boolean PRINT_DEBUG = false;

    public static final int NUM_TICKS_TO_FADE_MOUSE = 60;

    private final Vector3 clickLocation = new Vector3();

    private int leftClickTileX, leftClickTileY;
    private int rightClickTileX, rightClickTileY;
    private float mouseTileX, mouseTileY;
    private float mouseWorldX, mouseWorldY;

    @Setter
    private boolean invalidate = true;

    private Timer.Task waitForMouseFadeTask;

    @Getter
    private final FadeOut fadeOut = new FadeOut();

    @Getter
    @Setter
    private boolean highlightHoverTile = false;

    private boolean foundClick = false;

    void mouseMove(final int screenX, final int screenY) {
        final Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = tiledMapCoordinates.x / ClientConstants.TILE_SIZE;
        this.mouseTileY = tiledMapCoordinates.y / ClientConstants.TILE_SIZE;
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

    void mouseDragged(int buttonDown, final int screenX, final int screenY) {
        final Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.mouseTileX = tiledMapCoordinates.x / ClientConstants.TILE_SIZE;
        this.mouseTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        // Place tile in world
        if (buttonDown == Input.Buttons.LEFT) {
            ClientMain.getInstance().getWorldBuilder().placeTile(getMouseTileX(), getMouseTileY());
        }
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
        return ClientMain.getInstance().getGameScreen().getCamera().unproject(clickLocation.set(screenX, screenY, 0));
    }

    private boolean entityClickTest(float drawX, float drawY) {
        if (mouseWorldX >= drawX && mouseWorldX < drawX + 16) {
            return mouseWorldY >= drawY && mouseWorldY < drawY + 16;
        }
        return false;
    }

    private void left(final int screenX, final int screenY) {
        foundClick = false;
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.leftClickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.leftClickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        // Clear scroll focus so map zooming can resume.
        ClientMain.getInstance().getStageHandler().getStage().setScrollFocus(null);

        // Place tile in world
        ClientMain.getInstance().getWorldBuilder().placeTile(getMouseTileX(), getMouseTileY());

        // If setting the spawn of an entity, prevent the mouse from making the player walk.
        EntityEditor entityEditor = ActorUtil.getStageHandler().getEntityEditor();
        if (entityEditor != null) {
            if (entityEditor.getNpcTab().isSelectSpawnActivated()) return;
            if (entityEditor.getMonsterTab().isSelectSpawnActivated()) return;
        }

        // Toggle clicked door
        ClientMain.getInstance().getDoorManager().playerClientToggleDoor(leftClickTileX, leftClickTileY);

        clickAIEntities();
        clickPlayerEntities();
        clickSkillNodes();
        clickItemStackDrops();
        clickToWalkToPath();
    }

    private void clickAIEntities() {
        if (foundClick) return;
        for (final MovingEntity movingEntity : EntityManager.getInstance().getAiEntityList().values()) {
            if (entityClickTest(movingEntity.getDrawX(), movingEntity.getDrawY())) {
                if (movingEntity instanceof AiEntity) {
                    switch (((AiEntity) movingEntity).getFirstInteraction()) {
                        case TALK:
                            EntityInteract.talkNPC((NPC) movingEntity);
                            break;
                        case SHOP:
                            EntityInteract.openShop((AiEntity) movingEntity);
                            break;
                        case BANK:
                            EntityInteract.openBank(movingEntity);
                            break;
                        case ATTACK:
                        default:
                            ClientMain.getInstance().getEntityTracker().follow(movingEntity);
                            break;
                    }
                    EntityManager.getInstance().getPlayerClient().setTargetEntity(movingEntity);
                }
                foundClick = true;
                return;
            }
        }
    }

    private void clickPlayerEntities() {
        if (foundClick) return;
        for (MovingEntity movingEntity : EntityManager.getInstance().getPlayerEntityList().values()) {
            if (entityClickTest(movingEntity.getDrawX(), movingEntity.getDrawY())) {
                ClientMain.getInstance().getEntityTracker().follow(movingEntity);
                EntityManager.getInstance().getPlayerClient().setTargetEntity(movingEntity);
                foundClick = true;
                return;
            }
        }
    }

    private void clickSkillNodes() {
//        if (foundClick) return;
        // Skill nodes like Mining and Fishing etc
//        Queue<MoveNode> moveNodes = null;
//        for (StationaryEntity stationaryEntity : EntityManager.getInstance().getStationaryEntityList().values()) {
//            if (entityClickTest(stationaryEntity.getDrawX(), stationaryEntity.getDrawY())) {
//                Location location = stationaryEntity.getCurrentMapLocation();
//
//                if (clientLocation.isWithinDistance(location, (short) 1)) {
//                    // The player is requesting to interact with the entity.
//                    if (!MoveUtil.isEntityMoving(playerClient)) {
//                        new ClickActionPacketOut(new ClickAction(ClickAction.LEFT, stationaryEntity)).sendPacket();
//                    }
//                } else {
//                    // New Entity click so lets cancelFollow entityTracker
//                    ClientMain.getInstance().getEntityTracker().cancelFollow();
//
//                    // Top right quad
//                    Queue<MoveNode> testMoveNodes = pathFinding.findPath(clientLocation.getX(), clientLocation.getY(), leftClickTileX, leftClickTileY, clientLocation.getWorldName(), true);
//                    if (testMoveNodes == null) break;
//                    moveNodes = new LinkedList<MoveNode>();
//                    for (int i = testMoveNodes.size() - 1; i > 0; i--) {
//                        moveNodes.add(testMoveNodes.remove());
//                    }
//
//        foundClick = true;
//                }
//                break;
//            }
//        }
    }

    private void clickItemStackDrops() {
        if (foundClick) return;
        // Picking up ItemStacks from the ground
        for (ItemStackDrop itemStackDrop : EntityManager.getInstance().getItemStackDropList().values()) {
            if (entityClickTest(itemStackDrop.getDrawX(), itemStackDrop.getDrawY())) {
                EntityInteract.pickUpItemStackDrop(itemStackDrop);
                foundClick = true;
                break;
            }
        }
    }

    private void clickToWalkToPath() {
        if (foundClick) return;

        RegionManager regionManager = ClientMain.getInstance().getRegionManager();

        StageHandler stageHandler = ClientMain.getInstance().getStageHandler();
        TileBuildMenu tileBuildMenu = stageHandler.getTileBuildMenu();

        // See if we need to stop click to walk
        if (tileBuildMenu != null && tileBuildMenu.isVisible()) {
            WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
            if (!worldBuilder.isAllowClickToMove()) return;
        }

        if (regionManager.isEditRegion()) return;

        // Click to walk path finding
        ClientMain.getInstance().getEntityTracker().walkTo(
                getLeftClickTileX(),
                getLeftClickTileY(),
                EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().getZ(),
                false);
    }

    private void middle(final int screenX, final int screenY) {
        println(getClass(), "Middle Pressed: " + screenX + "/" + screenY, false, PRINT_DEBUG);
    }

    private void right(final int screenX, final int screenY) {
        Vector3 tiledMapCoordinates = cameraXYtoTiledMapXY(screenX, screenY);
        this.rightClickTileX = (int) (tiledMapCoordinates.x / ClientConstants.TILE_SIZE);
        this.rightClickTileY = (int) (tiledMapCoordinates.y / ClientConstants.TILE_SIZE);

        // Toggle clicked door
        ClientMain.getInstance().getDoorManager().playerClientToggleDoor(rightClickTileX, rightClickTileY);

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
            ActorUtil.getStageHandler().getEntityDropDownMenu().toggleMenu(entityList, screenX, ClientMain.getInstance().getGameScreen().getCamera().viewportHeight - screenY);
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

    private final CursorDrawType lastCursorDrawType = CursorDrawType.NO_DRAWABLE;
    private TextureRegionDrawable cursorDrawable;

    // TODO: FIX THIS....
    public void drawMovingMouse(PlayerClient playerClient, SpriteBatch spriteBatch) {
//        GameWorld gameMap = playerClient.getGameWorld();
//        if (WorldUtil.isOutOfBounds(gameMap, mouseTileX, mouseTileY)) return;
////        CursorDrawType cursorDrawType = gameMap.get[mouseTileX][mouseTileY].getCursorDrawType();
//        // [location.getX() + location.getY() * location.getGameWorld().getWorldWidthInChunks()]
//
//        if (cursorDrawType != lastCursorDrawType && cursorDrawType != CursorDrawType.NO_DRAWABLE) {
//            lastCursorDrawType = cursorDrawType;
//            cursorDrawable = new ImageBuilder(GameAtlas.CURSOR, cursorDrawType.getDrawableRegion(), cursorDrawType.getSize()).buildTextureRegionDrawable();
//        }
//
//        if (cursorDrawType == CursorDrawType.NO_DRAWABLE) return;
//        fadeOut.draw(spriteBatch,
//                cursorDrawable,
//                mouseWorldX - cursorDrawType.getSize() / 2f,
//                mouseWorldY - cursorDrawType.getSize() / 2f,
//                cursorDrawType.getSize(),
//                cursorDrawType.getSize());
    }

    public void drawMoveNodes(SpriteBatch spriteBatch) {
        if (ClientMain.getInstance().getClientMovementProcessor().getCurrentMovementInput() == ClientMovementProcessor.MovementInput.MOUSE) {
            Queue<MoveNode> remainingMoveNodes = ClientMain.getInstance().getClientPlayerMovementManager().getMovements();
            for (MoveNode moveNode : remainingMoveNodes) {
                spriteBatch.draw(new ImageBuilder(GameAtlas.CURSOR, "path_find").buildTextureRegionDrawable().getRegion(), moveNode.getWorldX() * ClientConstants.TILE_SIZE, moveNode.getWorldY() * ClientConstants.TILE_SIZE);
            }
        }
    }

    public void drawMouseHoverIcon(SpriteBatch spriteBatch, Texture validTileLocationTexture, Texture invalidTileLocationTexture) {
        if (isHighlightHoverTile()) {
            float x = getMouseTileX() * ClientConstants.TILE_SIZE;
            float y = getMouseTileY() * ClientConstants.TILE_SIZE;
            if (WorldUtil.isTraversable(Math.round(getMouseTileX()), Math.round(getMouseTileY()))) {
                spriteBatch.draw(validTileLocationTexture, x, y, ClientConstants.TILE_SIZE, ClientConstants.TILE_SIZE);
            } else {
                spriteBatch.draw(invalidTileLocationTexture, x, y, ClientConstants.TILE_SIZE, ClientConstants.TILE_SIZE);
            }
        }
    }

    public void invalidateMouse() {
        invalidate = true;
    }

    public int getMouseTileX() {
        // If the mouse goes into the negatives, fix the value.
        if (mouseTileX < 0) return (int) mouseTileX - 1;
        return (int) mouseTileX;
    }

    public int getMouseTileY() {
        // If the mouse goes into the negatives, fix the value.
        if (mouseTileY < 0) return (int) mouseTileY - 1;
        return (int) mouseTileY;
    }

    public int getLeftClickTileX() {
        // If the mouse goes into the negatives, fix the value.
        if (leftClickTileX < 0) return leftClickTileX - 1;
        return leftClickTileX;
    }

    public int getLeftClickTileY() {
        // If the mouse goes into the negatives, fix the value.
        if (leftClickTileY < 0) return leftClickTileY - 1;
        return leftClickTileY;
    }

    public int getRightClickTileX() {
        // If the mouse goes into the negatives, fix the value.
        if (rightClickTileX < 0) return rightClickTileX - 1;
        return rightClickTileX;
    }

    public int getRightClickTileY() {
        // If the mouse goes into the negatives, fix the value.
        if (rightClickTileY < 0) return rightClickTileY - 1;
        return rightClickTileY;
    }
}
