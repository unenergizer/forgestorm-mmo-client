package com.forgestorm.client.game.world.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import space.earlygrey.shapedrawer.ShapeDrawer;

@SuppressWarnings("DanglingJavadoc")
public class RegionManager {

    private static final Color BACKGROUND_COLOR = new Color(255, 0, 0, .4f);
    private static final Color BORDER_COLOR = Color.RED;
    private static final Color HIGHLIGHT_EDGE_COLOR = Color.YELLOW;

    private final Map<Integer, Region> regionMap;


    @Getter
    @Setter
    private boolean editRegion = false;

    @Getter
    @Setter
    private boolean drawRegion = false;

    @Getter
    @Setter
    private Region regionToEdit;

    private DragArea dragArea;

    private int lastMouseX, lastMouseY;


    public RegionManager() {
        regionMap = ClientMain.getInstance().getFileManager().getRegionData().getRegionMap();

        regionToEdit = regionMap.get(1);
    }

    public void editRegion(ShapeDrawer shapeDrawer) {

        if (!drawRegion) return;

        MouseManager mouseManager = ClientMain.getInstance().getMouseManager();
        int mouseTileX = mouseManager.getMouseTileX();
        int mouseTileY = mouseManager.getMouseTileY();

        // Highlight requirements
        boolean inBoundsX = mouseTileX >= regionToEdit.getWorld1X() && mouseTileX < regionToEdit.getWorld1X() + regionToEdit.getWorld2X();
        boolean inBoundsY = mouseTileY >= regionToEdit.getWorld1Y() && mouseTileY < regionToEdit.getWorld1Y() + regionToEdit.getWorld2Y();

        // Get inside (-1) and outside edge
        boolean topEdge = mouseTileY == regionToEdit.getWorld2Y() - 1 || mouseTileY == regionToEdit.getWorld2Y();
        boolean bottomEdge = mouseTileY == regionToEdit.getWorld1Y() - 1 || mouseTileY == regionToEdit.getWorld1Y();
        boolean leftEdge = mouseTileX == regionToEdit.getWorld1X() - 1 || mouseTileX == regionToEdit.getWorld1X();
        boolean rightEdge = mouseTileX == regionToEdit.getWorld2X() - 1 || mouseTileX == regionToEdit.getWorld2X();

        // Are we inside the region?
        boolean centerArea = mouseTileX > regionToEdit.getWorld1X()
                && mouseTileX < regionToEdit.getWorld2X() - 1
                && mouseTileY > regionToEdit.getWorld1Y()
                && mouseTileY < regionToEdit.getWorld2Y() - 1;

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

                    regionToEdit.setWorld1X(regionToEdit.getWorld1X() + differenceMoveX);
                    regionToEdit.setWorld1Y(regionToEdit.getWorld1Y() + differenceMoveY);
                    regionToEdit.setWorld2X(regionToEdit.getWorld2X() + differenceMoveX);
                    regionToEdit.setWorld2Y(regionToEdit.getWorld2Y() + differenceMoveY);
                    break;
                case TOP:
                    regionToEdit.setWorld2Y(mouseTileY);
                    break;
                case BOTTOM:
                    regionToEdit.setWorld1Y(mouseTileY);
                    break;
                case LEFT:
                    regionToEdit.setWorld1X(mouseTileX);
                    break;
                case RIGHT:
                    regionToEdit.setWorld2X(mouseTileX);
                    break;
            }
        }

        // Draw cords
        int drawX1 = regionToEdit.getWorld1X() * ClientConstants.TILE_SIZE;
        int drawY1 = regionToEdit.getWorld1Y() * ClientConstants.TILE_SIZE;
        int drawX2 = (regionToEdit.getWorld2X() + 1) * ClientConstants.TILE_SIZE;
        int drawY2 = (regionToEdit.getWorld2Y() + 1) * ClientConstants.TILE_SIZE;

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
