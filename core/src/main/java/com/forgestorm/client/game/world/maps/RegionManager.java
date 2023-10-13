package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.audio.MusicManager;
import com.forgestorm.client.game.audio.SoundManager;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.actors.dev.world.RegionEditor;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;
import com.forgestorm.client.util.yaml.YamlUtil;
import lombok.Getter;
import lombok.Setter;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.io.File;
import java.util.Map;

@SuppressWarnings("DanglingJavadoc")
public class RegionManager {

    private static final Color BACKGROUND_COLOR = new Color(255, 0, 0, .4f);
    private static final Color BORDER_COLOR = Color.RED;
    private static final Color HIGHLIGHT_EDGE_COLOR = Color.YELLOW;

    private final ClientMain clientMain;

    private Map<Integer, Region> regionMap;

    @Getter
    @Setter
    private boolean editRegion = false;

    @Getter
    @Setter
    private boolean drawRegion = false;

    @Getter
    private Region regionToEdit;

    private DragArea dragArea;

    private int lastMouseX, lastMouseY;

    /**
     * Current player region state
     */
    private Region playerCurrentRegion;
    private boolean insideRegion = false;
    private boolean outsideRegion = true;

    public RegionManager(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    public boolean isPlayerCurrentRegion(Region region) {
        return playerCurrentRegion.getRegionID() == region.getRegionID();
    }

    public void playerEnterLocation(Location futureLocation) {
        Region regionFound = findRegion(
                futureLocation.getX(),
                futureLocation.getY(),
                futureLocation.getZ());

        boolean isSameRegion = regionFound != null
                && playerCurrentRegion != null
                && regionFound.getRegionID() == playerCurrentRegion.getRegionID();

        // Grab outer classes
        ChatWindow chatWindow = clientMain.getStageHandler().getChatWindow();
        MusicManager musicManager = clientMain.getAudioManager().getMusicManager();
        SoundManager soundManager = clientMain.getAudioManager().getSoundManager();

        // EXECUTE REGION TASKS!
        if (!insideRegion && !isSameRegion && regionFound != null && playerCurrentRegion == null) {

            // #################################
            // ### DO ENTER TASKS ##############
            // #################################

            // GREETINGS CHAT MESSAGE
            if (regionFound.getGreetingsChat() != null) {
                chatWindow.appendChatMessage(ChatChannelType.GENERAL, "[PINK]" + regionFound.getGreetingsChat());
            }

            // PLAY BACKGROUND MUSIC
            if (regionFound.getBackgroundMusicID() != null) {
                musicManager.playMusic(getClass(), (short) (int) regionFound.getBackgroundMusicID());
            }

            // PLAY AMBIENCE SOUND
            if (regionFound.getAmbianceSoundID() != null) {
                soundManager.playSoundFx(getClass(), (short) (int) regionFound.getAmbianceSoundID());
            }

            // TODO: Next flag here..........

            // Set found region...
            // Set flip-flop booleans
            playerCurrentRegion = regionFound;
            insideRegion = true;
            outsideRegion = false;
        } else if (!outsideRegion && !isSameRegion && playerCurrentRegion != null) {

            // #################################
            // ### DO EXIT TASKS ###############
            // #################################

            // FAREWELL CHAT MESSAGE
            if (playerCurrentRegion.getFarewellChat() != null) {
                chatWindow.appendChatMessage(ChatChannelType.GENERAL, "[PINK]" + playerCurrentRegion.getFarewellChat());
            }

            // STOP BACKGROUND MUSIC
            if (playerCurrentRegion.getBackgroundMusicID() != null) {
                musicManager.stopMusic(false);
            }

            // STOP AMBIENCE SOUND
            if (playerCurrentRegion.getAmbianceSoundID() != null) {
                soundManager.stopSoundFx(false);
            }

            // TODO: Next flag here..........

            // Set current region to null...
            // Set flip-flop booleans
            playerCurrentRegion = null;
            insideRegion = false;
            outsideRegion = true;
        }
    }

    private Region findRegion(int x, int y, short z) {
        for (Region region : regionMap.values()) {
            if (region.doesIntersect(x, y, z)) {
                return region;
            }
        }
        return null;
    }

    public void setRegionMap(Map<Integer, Region> regionMap) {
        this.regionMap = regionMap;
        changeRegion(0);

        clientMain.getStageHandler().getRegionEditor().updateRegionSelectionList(this.regionMap);
    }

    public void changeRegion(int regionID) {
        this.regionToEdit = regionMap.get(regionID);

        // Update UI
        RegionEditor regionEditor = clientMain.getStageHandler().getRegionEditor();
        if (regionEditor != null && regionEditor.isVisible())
            regionEditor.updateFlagsTable(regionToEdit);
    }

    public void createRegion() {

        // Find unused regionID;
        int regionID = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!regionMap.containsKey(i)) {
                regionID = i;
                break;
            }
        }

        // Create the region
        final int spacer = 3;
        Location location = clientMain.getEntityManager().getPlayerClient().getCurrentMapLocation();
        Region region = new Region(
                regionID,
                location.getWorldName(),
                location.getX() - spacer,
                location.getY() - spacer,
                location.getX() + spacer,
                location.getY() + spacer,
                location.getZ());

        regionMap.put(regionID, region);
        changeRegion(regionID);

        // Update the region list
        clientMain.getStageHandler().getRegionEditor().updateRegionSelectionList(this.regionMap);
    }

    public void deleteRegion() {
        regionMap.entrySet().removeIf(regionEntry -> regionEntry.getValue().getRegionID() == regionToEdit.getRegionID());

        if (regionMap.isEmpty()) {
            createRegion();
        } else {
            for (Region region : regionMap.values()) {
                if (region != null) {
                    changeRegion(region.getRegionID());
                    break;
                }
            }
        }

        // Update the region list
        clientMain.getStageHandler().getRegionEditor().updateRegionSelectionList(this.regionMap);
    }

    public void saveRegionsToFile() {
        String filePath = clientMain.getFileManager().getClientFilesDirectoryPath() + File.separator + "Regions.yaml";
        YamlUtil.saveYamlToFile(regionMap, filePath);
    }

    public void editRegion(ShapeDrawer shapeDrawer) {

        if (!drawRegion) return;

        MouseManager mouseManager = clientMain.getMouseManager();
        int mouseTileX = mouseManager.getMouseTileX();
        int mouseTileY = mouseManager.getMouseTileY();

        // Highlight requirements
        boolean inBoundsX = mouseTileX >= regionToEdit.getX1() && mouseTileX < regionToEdit.getX1() + regionToEdit.getX2();
        boolean inBoundsY = mouseTileY >= regionToEdit.getY1() && mouseTileY < regionToEdit.getY1() + regionToEdit.getY2();

        // Get inside (-1) and outside edge
        boolean topEdge = mouseTileY == regionToEdit.getY2() - 1 || mouseTileY == regionToEdit.getY2();
        boolean bottomEdge = mouseTileY == regionToEdit.getY1() - 1 || mouseTileY == regionToEdit.getY1();
        boolean leftEdge = mouseTileX == regionToEdit.getX1() - 1 || mouseTileX == regionToEdit.getX1();
        boolean rightEdge = mouseTileX == regionToEdit.getX2() - 1 || mouseTileX == regionToEdit.getX2();

        // Are we inside the region?
        boolean centerArea = mouseTileX > regionToEdit.getX1()
                && mouseTileX < regionToEdit.getX2() - 1
                && mouseTileY > regionToEdit.getY1()
                && mouseTileY < regionToEdit.getY2() - 1;

        // Change region cords
        boolean leftPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        // TODO: Implement corner drag
        if (editRegion && !leftPressed) {
            /** Adjust the cords of the region when dragging */
            if (centerArea) {
                dragArea = DragArea.CENTER;
            } else if (topEdge && inBoundsX) {
                dragArea = DragArea.TOP;
            } else if (bottomEdge && inBoundsX) {
                dragArea = DragArea.BOTTOM;
            } else if (leftEdge && inBoundsY) {
                dragArea = DragArea.LEFT;
            } else if (rightEdge && inBoundsY) {
                dragArea = DragArea.RIGHT;
            } else {
                dragArea = null;
            }
        } else if (editRegion && leftPressed && dragArea != null) {
            /** Set the drag edge */
            switch (dragArea) {
                case CENTER:
                    int differenceMoveX = mouseTileX - lastMouseX;
                    int differenceMoveY = mouseTileY - lastMouseY;

                    regionToEdit.setX1(regionToEdit.getX1() + differenceMoveX);
                    regionToEdit.setY1(regionToEdit.getY1() + differenceMoveY);
                    regionToEdit.setX2(regionToEdit.getX2() + differenceMoveX);
                    regionToEdit.setY2(regionToEdit.getY2() + differenceMoveY);
                    break;
                case TOP:
                    regionToEdit.setY2(mouseTileY);
                    break;
                case BOTTOM:
                    regionToEdit.setY1(mouseTileY);
                    break;
                case LEFT:
                    regionToEdit.setX1(mouseTileX);
                    break;
                case RIGHT:
                    regionToEdit.setX2(mouseTileX);
                    break;
            }
        }

        // Draw cords
        int drawX1 = regionToEdit.getX1() * ClientConstants.TILE_SIZE;
        int drawY1 = regionToEdit.getY1() * ClientConstants.TILE_SIZE;
        int drawX2 = (regionToEdit.getX2() + 1) * ClientConstants.TILE_SIZE;
        int drawY2 = (regionToEdit.getY2() + 1) * ClientConstants.TILE_SIZE;

        // Select edge color
        // Right here we are assuming the center area is selected
        Color topColor = HIGHLIGHT_EDGE_COLOR;
        Color bottomColor = HIGHLIGHT_EDGE_COLOR;
        Color leftColor = HIGHLIGHT_EDGE_COLOR;
        Color rightColor = HIGHLIGHT_EDGE_COLOR;

        if (!centerArea) {
            topColor = topEdge && inBoundsX && !leftPressed || dragArea == DragArea.TOP && leftPressed ? HIGHLIGHT_EDGE_COLOR : BORDER_COLOR;
            bottomColor = bottomEdge && inBoundsX && !leftPressed || dragArea == DragArea.BOTTOM && leftPressed ? HIGHLIGHT_EDGE_COLOR : BORDER_COLOR;
            leftColor = leftEdge && inBoundsY && !leftPressed || dragArea == DragArea.LEFT && leftPressed ? HIGHLIGHT_EDGE_COLOR : BORDER_COLOR;
            rightColor = rightEdge && inBoundsY && !leftPressed || dragArea == DragArea.RIGHT && leftPressed ? HIGHLIGHT_EDGE_COLOR : BORDER_COLOR;
        }

        drawRegion(shapeDrawer, drawX1, drawY1, drawX2, drawY2, topColor, bottomColor, leftColor, rightColor);

        // Record last mouse location
        lastMouseX = mouseTileX;
        lastMouseY = mouseTileY;

        // Update UI
        RegionEditor regionEditor = clientMain.getStageHandler().getRegionEditor();
        if (regionEditor != null && regionEditor.isVisible()) regionEditor.updateRegionInfo();
    }

    public void drawRegion(ShapeDrawer shapeDrawer, int drawX1, int drawY1, int drawX2, int drawY2, Color topColor, Color bottomColor, Color leftColor, Color rightColor) {
        shapeDrawer.filledRectangle(drawX1, drawY1, drawX2 - drawX1, drawY2 - drawY1, BACKGROUND_COLOR); // Draw fill rectangle
        shapeDrawer.line(drawX1, drawY2, drawX2, drawY2, topColor);     // Drawl Top
        shapeDrawer.line(drawX1, drawY1, drawX2, drawY1, bottomColor);  // Drawl Bottom
        shapeDrawer.line(drawX1, drawY1, drawX1, drawY2, leftColor);    // Drawl Left
        shapeDrawer.line(drawX2, drawY1, drawX2, drawY2, rightColor);   // Drawl Right
    }

    enum DragArea {
        CENTER,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    enum RegionType {
        BUILDING
    }
}
