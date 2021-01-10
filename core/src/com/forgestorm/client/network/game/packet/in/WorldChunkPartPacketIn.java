package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileImage;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.game.world.maps.WorldManager;
import com.forgestorm.client.game.world.maps.building.LayerDefinition;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.WORLD_CHUNK_LAYER)
public class WorldChunkPartPacketIn implements PacketListener<WorldChunkPartPacketIn.WorldChunkPartPacket> {

    private static final boolean PRINT_DEBUG = false;

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
    private final WorldManager worldManager = ClientMain.getInstance().getWorldManager();

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        // Read chunk location
        short chunkX = clientHandler.readShort();
        short chunkY = clientHandler.readShort();

        // Get layer information
        LayerDefinition layerDefinition = LayerDefinition.getLayerDefinition(clientHandler.readByte());
        byte sectionSent = clientHandler.readByte();

        // Read layer part
        TileImage[] layerParts = new TileImage[ClientConstants.MAX_TILE_GET];

        for (int i = 0; i < ClientConstants.MAX_TILE_GET; i++) {
            int tileImageID = clientHandler.readInt();
            if (tileImageID == 0) {
                layerParts[i] = null;
            } else {
                layerParts[i] = worldBuilder.getTileImage(tileImageID);
            }
        }

        println(getClass(), "Receiving chunk section! Layer: " + layerDefinition + ", Section: " + sectionSent, true, PRINT_DEBUG);

        return new WorldChunkPartPacket(chunkX, chunkY, layerDefinition, sectionSent, layerParts);
    }

    @Override
    public void onEvent(WorldChunkPartPacket packetData) {
        GameWorld gameWorld = worldManager.getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.getChunk(packetData.chunkX, packetData.chunkY);

        if (worldChunk == null) {
            // Generate new chunk and add it to the game world
            WorldChunk newChunk = new WorldChunk(packetData.chunkX, packetData.chunkY);
            newChunk.setTileImages(packetData.layerDefinition, packetData.sectionSent, packetData.layerParts);
            gameWorld.setChunk(newChunk);
        } else {
            worldChunk.setTileImages(packetData.layerDefinition, packetData.sectionSent, packetData.layerParts);
        }
    }

    @AllArgsConstructor
    class WorldChunkPartPacket extends PacketData {
        private final short chunkX, chunkY;
        private final LayerDefinition layerDefinition;
        private final byte sectionSent;
        private final TileImage[] layerParts;
    }
}