package com.valenguard.client.game.maps.file;

import com.badlogic.gdx.files.FileHandle;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.GameMap;
import com.valenguard.client.game.maps.data.Tile;
import com.valenguard.client.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@SuppressWarnings({"ConstantConditions", "SpellCheckingInspection"})
public class TmxFileParser {

    private static final boolean PRINT_DEBUG = false;

    /**
     * This takes in a TMX map and gets the collision elements from it and builds a collision
     * array for checking entity collision server side.
     *
     * @return A map data class with information about this map.
     */
    public static GameMap loadXMLFile(FileHandle fileHandle) {
        Log.println(TmxFileParser.class, "Tmx Parsing: " + fileHandle.file().getAbsolutePath(), true, PRINT_DEBUG);

        // Lets get the document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document = null;
        try {
            document = builder.parse(fileHandle.read());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (document.getDocumentElement() == null)
            Log.println(TmxFileParser.class, "Document is null?? 2");

        // Get the first element
        Element tmx = document.getDocumentElement();

        /* *********************************************************************************************
         * BUILD TILED MAP TILES......
         ***********************************************************************************************/

        final int mapWidth = Integer.parseInt(tmx.getAttributes().getNamedItem("width").getNodeValue());
        final int mapHeight = Integer.parseInt(tmx.getAttributes().getNamedItem("height").getNodeValue());

        Log.println(TmxFileParser.class, "MapWidth: " + mapWidth, false, PRINT_DEBUG);
        Log.println(TmxFileParser.class, "MapHeight: " + mapHeight, false, PRINT_DEBUG);

        Tile map[][] = new Tile[mapWidth][mapHeight];

        // Examine XML file and find tags called "layer" then loop through them.
        NodeList layerTag = tmx.getElementsByTagName("layer");

        for (int i = 0; i < layerTag.getLength(); i++) {

            // Get collision layer
            if (((Element) layerTag.item(i)).getAttribute("name").equals("collision")) {

                // Get collision data
                NodeList collisionData = ((Element) layerTag.item(i)).getElementsByTagName("data");

                // Get Array of tiles
                String[] tiles = collisionData.item(0).getTextContent().split(",");

                // Loop through all tiles
                // Start iteration from top left
                int currentY = mapHeight - 1; // using map height instead of zero, because we are starting at top.
                int currentX = 0; // using 0 because we are starting from the left

                for (String tile : tiles) {

                    tile = tile.trim();

                    // Tile ID
                    int tileType = Integer.parseInt(tile);

                    // Basic coordinate system of our tiled map.
                    // Iteration starts at top left.
                    // So if map height is 50, we start fromY at 50
                    // and then count down (moving down the y access).
                    // Since X starts on the left, we will start it at 0.
                    //
                    // [X,Y]
                    //
                    //////////////////////////////////////
                    // [0,5] [1,5] [2,5] [3,5] [4,5] [5,5]
                    // [0,4] [1,4] [2,4] [3,4] [4,4] [5,4]
                    // [0,3] [1,3] [2,3] [3,3] [4,3] [5,3]
                    // [0,2] [1,2] [2,2] [3,2] [4,2] [5,2]
                    // [0,1] [1,1] [2,1] [3,1] [4,1] [5,1]
                    // [0,0] [1,0] [2,0] [3,0] [4,0] [5,0]

                    // Initializing the instance of the new tile
                    map[currentX][currentY] = new Tile();

                    // Check for tile ID and addUi it to collision map
                    if (tileType != 0) map[currentX][currentY].setTraversable(false);
                    else map[currentX][currentY].setTraversable(true);

                    // Increment x horizontal value
                    currentX++;

                    // Check for end of map width
                    if (currentX == mapWidth) {
                        currentX = 0; // reset x counter
                        currentY--; // decrement y value
                    }
                }
            }
        }

        /* *********************************************************************************************
         * GET SPECIFIC TILE ATTRIBUTES - WARNING: Element names are CASE sensitive!
         ***********************************************************************************************/

        // Examine XML file and find tags called "layer" then loop through them.
        NodeList objectGroupTag = tmx.getElementsByTagName("objectgroup");

        for (int i = 0; i < objectGroupTag.getLength(); i++) {

            // Get warps
            if (((Element) objectGroupTag.item(i)).getAttribute("name").equals("warp")) {

                NodeList objectTag = ((Element) objectGroupTag.item(i)).getElementsByTagName("object");

                for (int j = 0; j < objectTag.getLength(); j++) {
                    if (objectTag.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

                    Element objectTagElement = (Element) objectTag.item(j);
                    int tmxFileX = Integer.parseInt(objectTagElement.getAttribute("x")) / ClientConstants.TILE_SIZE;
                    int tmxFileY = Integer.parseInt(objectTagElement.getAttribute("y")) / ClientConstants.TILE_SIZE;
                    int tmxFileWidth = Integer.parseInt(objectTagElement.getAttribute("width")) / ClientConstants.TILE_SIZE;
                    int tmxFileHeight = Integer.parseInt(objectTagElement.getAttribute("height")) / ClientConstants.TILE_SIZE;

                    String warpMapName;
                    int warpX, warpY;
                    MoveDirection moveDirection;
                    NodeList properties = objectTagElement.getElementsByTagName("properties").item(0).getChildNodes();

                    Log.println(TmxFileParser.class, "", false, PRINT_DEBUG);
                    Log.println(TmxFileParser.class, "===[ WARP ]==================================", true, PRINT_DEBUG);

                    for (int k = 0; k < properties.getLength(); k++) {

                        if (properties.item(k).getNodeType() != Node.ELEMENT_NODE) continue;
                        Element propertyElement = (Element) properties.item(k);

                        // Get map name:
                        if (propertyElement.getAttribute("name").equals("mapname")) {
                            warpMapName = propertyElement.getAttribute("value");
                            Log.println(TmxFileParser.class, "WarpMap: " + warpMapName, false, PRINT_DEBUG);
                        }

                        // Get map X:
                        if (propertyElement.getAttribute("name").equals("x")) {
                            warpX = Integer.parseInt(propertyElement.getAttribute("value"));
                            Log.println(TmxFileParser.class, "WarpX: " + warpX, false, PRINT_DEBUG);
                        }

                        // Get map Y:
                        if (propertyElement.getAttribute("name").equals("y")) {
                            warpY = Integer.parseInt(propertyElement.getAttribute("value"));
                            Log.println(TmxFileParser.class, "WarpY: " + warpY, false, PRINT_DEBUG);
                        }

                        // Get map facing moveDirection:
                        if (propertyElement.getAttribute("name").equals("direction")) {
                            moveDirection = MoveDirection.valueOf(propertyElement.getAttribute("value").toUpperCase());
                            Log.println(TmxFileParser.class, "WarpDirection: " + moveDirection, false, PRINT_DEBUG);
                        }
                    }

                    // Print the map to console.
                    for (int ii = tmxFileY; ii < tmxFileY + tmxFileHeight; ii++) {
                        for (int jj = tmxFileX; jj < tmxFileX + tmxFileWidth; jj++) {
                            Tile tile = map[jj][mapHeight - ii - 1];
                            tile.setWarp(true);
                        }
                    }
                }
            }
        }

        /*
         * Print the map to console.
         */
        if (PRINT_DEBUG) {
            int yOffset = mapHeight - 1;
            for (int height = yOffset; height >= 0; height--) {
                for (int width = 0; width < mapWidth; width++) {
                    Tile tile = map[width][height];
                    if (!tile.isTraversable()) System.out.print("X");
                    else if (tile.isTraversable() && tile.isWarp()) System.out.print("@");
                    else if (tile.isTraversable() && !tile.isWarp()) System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println(); // Clear a line for next map
        }
        return new GameMap(fileHandle.file().getName().replace(".tmx", ""), mapWidth, mapHeight, map);
    }
}
