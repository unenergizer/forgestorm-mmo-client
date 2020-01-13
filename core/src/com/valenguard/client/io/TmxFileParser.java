package com.valenguard.client.io;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.game.world.maps.CursorDrawType;
import com.valenguard.client.game.world.maps.GameMap;
import com.valenguard.client.game.world.maps.Tile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.valenguard.client.util.Log.println;

public class TmxFileParser {

    private static final boolean PRINT_DEBUG = false;

    /**
     * This takes in a TMX map and gets the collision elements from it and builds a collision
     * array for checking entity collision server side.
     *
     * @return A map data class with information about this map.
     */
    public static GameMap loadXMLFile(FileHandle fileHandle) {
        Document document = null;

        // Lets get the document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(fileHandle.read()); // LibGDX fileHandle.read() for Android/PC
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the first element
        Element tmx = document.getDocumentElement();

        final short mapWidth = Short.parseShort(tmx.getAttributes().getNamedItem("width").getNodeValue());
        final short mapHeight = Short.parseShort(tmx.getAttributes().getNamedItem("height").getNodeValue());
        Color backgroundColor = null;
        if (tmx.getAttributes().getNamedItem("backgroundcolor") != null) {
            backgroundColor = Color.valueOf(tmx.getAttributes().getNamedItem("backgroundcolor").getNodeValue());
        }
        Tile[][] mapTiles = parseMapTiles(mapWidth, mapHeight, tmx);

        // Examine XML file and find tags called "layer" then loop through them.
        // NOTE: Element names are CASE sensitive!
        NodeList objectGroupTag = tmx.getElementsByTagName("objectgroup");
        for (int i = 0; i < objectGroupTag.getLength(); i++) {

            // Get Bank Access
            if (((Element) objectGroupTag.item(i)).getAttribute("name").equals("bank_access")) {
                NodeList objectTag = ((Element) objectGroupTag.item(i)).getElementsByTagName("object");
                processBankAccess(mapHeight, mapTiles, objectTag);
            }

            // Get Skill Nodes
            if (((Element) objectGroupTag.item(i)).getAttribute("name").equals("skill")) {
                NodeList objectTag = ((Element) objectGroupTag.item(i)).getElementsByTagName("object");
                processSkillNodes(mapHeight, mapTiles, objectTag);
            }

            // Get Warps
            if (((Element) objectGroupTag.item(i)).getAttribute("name").equals("warp")) {
                NodeList objectTag = ((Element) objectGroupTag.item(i)).getElementsByTagName("object");
                processWarps(mapHeight, mapTiles, objectTag);
            }

            // Get Signs
            if (((Element) objectGroupTag.item(i)).getAttribute("name").equals("sign")) {
                NodeList objectTag = ((Element) objectGroupTag.item(i)).getElementsByTagName("object");
                processSigns(mapHeight, mapTiles, objectTag);
            }
        }

        printMap(mapWidth, mapHeight, mapTiles);

        return new GameMap(fileHandle.file().getName().replace(".tmx", ""), mapWidth, mapHeight, mapTiles, backgroundColor);
    }

    private static Tile[][] parseMapTiles(short mapWidth, short mapHeight, Element tmx) {
        Tile[][] map = new Tile[mapWidth][mapHeight];

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
                short currentY = (short) (mapHeight - 1); // using map height instead of zero, because we are starting at top.
                short currentX = 0; // using 0 because we are starting from the left

                for (String tile : tiles) {

                    tile = tile.trim();

                    // Tile ID
                    int tileType = Integer.parseInt(tile);

                    // Basic coordinate system of our tiled map. Iteration starts at top left. So if map
                    // height is 50, we start fromY at 50 and then count down (moving down the y access).
                    // Since X starts on the left, we will start it at 0.
                    //
                    // [X,Y]
                    //
                    // [0,2] [1,2] [2,2]
                    // [0,1] [1,1] [2,1]
                    // [0,0] [1,0] [2,0]

                    // Initializing the instance of the new tile
                    map[currentX][currentY] = new Tile();

                    // Check for tile ID and addUi it to collision map
                    if (tileType != 0) {
                        map[currentX][currentY].setCursorDrawType(CursorDrawType.NOT_TRAVERSABLE);
                    } else {
                        map[currentX][currentY].addFlag(Tile.TRAVERSABLE);
                        map[currentX][currentY].setCursorDrawType(CursorDrawType.NO_DRAWABLE);
                    }

                    // Increment x horizontal value
                    currentX++;

                    // Check for end of map width
                    if (currentX == mapWidth) {
                        currentX = 0; // gameQuitReset x counter
                        currentY--; // decrement y value
                    }
                }
            }
        }
        return map;
    }

    private static void processBankAccess(short mapHeight, Tile[][] map, NodeList objectTag) {
        for (int j = 0; j < objectTag.getLength(); j++) {
            if (objectTag.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

            Element objectTagElement = (Element) objectTag.item(j);

            short x = (short) (Short.parseShort(objectTagElement.getAttribute("x")) / ClientConstants.TILE_SIZE);
            short y = (short) (mapHeight - (Short.parseShort(objectTagElement.getAttribute("y")) / ClientConstants.TILE_SIZE) - 1);

            println(TmxFileParser.class, "X = " + x, false, PRINT_DEBUG);
            println(TmxFileParser.class, "Y = " + y, false, PRINT_DEBUG);

            // Making it's associated tile non-traversable
            map[x][y].addFlag(Tile.BANK_ACCESS);

        }
    }

    private static void processSkillNodes(short mapHeight, Tile[][] map, NodeList objectTag) {
        for (int j = 0; j < objectTag.getLength(); j++) {

            if (objectTag.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

            Element objectTagElement = (Element) objectTag.item(j);

            short x = (short) (Short.parseShort(objectTagElement.getAttribute("x")) / ClientConstants.TILE_SIZE);
            short y = (short) (mapHeight - (Short.parseShort(objectTagElement.getAttribute("y")) / ClientConstants.TILE_SIZE) - 1);

            // Making it's associated tile non-traversable
            map[x][y].removeFlag(Tile.TRAVERSABLE);
            map[x][y].setCursorDrawType(CursorDrawType.MINING);
        }
    }

    private static void processWarps(short mapHeight, Tile[][] map, NodeList objectTag) {
        for (int j = 0; j < objectTag.getLength(); j++) {
            if (objectTag.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

            Element objectTagElement = (Element) objectTag.item(j);
            short tmxFileX = (short) (Short.parseShort(objectTagElement.getAttribute("x")) / ClientConstants.TILE_SIZE);
            short tmxFileY = (short) (Short.parseShort(objectTagElement.getAttribute("y")) / ClientConstants.TILE_SIZE);
            short tmxFileWidth = (short) (Short.parseShort(objectTagElement.getAttribute("width")) / ClientConstants.TILE_SIZE);
            short tmxFileHeight = (short) (Short.parseShort(objectTagElement.getAttribute("height")) / ClientConstants.TILE_SIZE);

            for (int ii = tmxFileY; ii < tmxFileY + tmxFileHeight; ii++) {
                for (int jj = tmxFileX; jj < tmxFileX + tmxFileWidth; jj++) {
                    Tile tile = map[jj][mapHeight - ii - 1];
                    tile.addFlag(Tile.WARP);
                    tile.setCursorDrawType(CursorDrawType.WARP);
                }
            }
        }
    }

    private static void processSigns(short mapHeight, Tile[][] map, NodeList objectTag) {
        for (int j = 0; j < objectTag.getLength(); j++) {
            if (objectTag.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

            Element objectTagElement = (Element) objectTag.item(j);
            short tmxFileX = (short) (Short.parseShort(objectTagElement.getAttribute("x")) / ClientConstants.TILE_SIZE);
            short tmxFileY = (short) (Short.parseShort(objectTagElement.getAttribute("y")) / ClientConstants.TILE_SIZE);
            short tmxFileWidth = (short) (Short.parseShort(objectTagElement.getAttribute("width")) / ClientConstants.TILE_SIZE);
            short tmxFileHeight = (short) (Short.parseShort(objectTagElement.getAttribute("height")) / ClientConstants.TILE_SIZE);

            int textId = (Integer.parseInt(objectTagElement.getAttribute("textId")));

            for (int ii = tmxFileY; ii < tmxFileY + tmxFileHeight; ii++) {
                for (int jj = tmxFileX; jj < tmxFileX + tmxFileWidth; jj++) {
                    Tile tile = map[jj][mapHeight - ii - 1];
                    tile.setTileText(textId);
                    tile.setCursorDrawType(CursorDrawType.WARP);
                }
            }
        }
    }

    /**
     * Print the map to console.
     */
    @SuppressWarnings("UnusedAssignment")
    private static void printMap(short mapWidth, short mapHeight, Tile[][] map) {
        if (!PRINT_DEBUG) return;
        println(TmxFileParser.class, "MapWidth: " + mapWidth, false);
        println(TmxFileParser.class, "MapHeight: " + mapHeight, false);
        short yOffset = (short) (mapHeight - 1);
        for (short height = yOffset; height >= 0; height--) {
            for (short width = 0; width < mapWidth; width++) {
                Tile tile = map[width][height];
                if (!tile.isFlagSet(Tile.TRAVERSABLE)) System.out.print("X");
                else if (tile.isFlagSet(Tile.TRAVERSABLE) && tile.isFlagSet(Tile.WARP))
                    System.out.print("@");
                else if (tile.isFlagSet(Tile.TRAVERSABLE) && !tile.isFlagSet(Tile.WARP))
                    System.out.print(" ");
            }
            println(true);
        }
        println(true); // Clear a line for next map
    }
}
