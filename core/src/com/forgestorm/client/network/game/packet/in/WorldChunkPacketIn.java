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

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.WORLD_CHUNK)
public class WorldChunkPacketIn implements PacketListener<WorldChunkPacketIn.WorldChunkPacket> {

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
    private final WorldManager worldManager = ClientMain.getInstance().getWorldManager();

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        Map<LayerDefinition, TileImage[]> layerDefinitionMap = new HashMap<LayerDefinition, TileImage[]>();

        // Read chunk location
        short chunkX = clientHandler.readShort();
        short chunkY = clientHandler.readShort();

        // Get number of layers the chunk has
        byte numberOfLayers = clientHandler.readByte();

        // Read chunk contents
        for (byte i = 0; i < numberOfLayers; i++) {

            // Write layerDefinition
            LayerDefinition layerDefinition = LayerDefinition.getLayerDefinition(clientHandler.readByte());
            TileImage[] tileImageArray = new TileImage[ClientConstants.CHUNK_SIZE];

            // Read all tile images.
            for (int j = 0; j < tileImageArray.length; j++) {
                int tileImageID = clientHandler.readInt();
                if (tileImageID == 0) {
                    tileImageArray[j] = null;
                } else {
                    tileImageArray[j] = worldBuilder.getTileImage(tileImageID);
                }
            }

            layerDefinitionMap.put(layerDefinition, tileImageArray);
        }

        return new WorldChunkPacket(chunkX, chunkY, layerDefinitionMap);
    }

    @Override
    public void onEvent(WorldChunkPacket packetData) {
        GameWorld gameWorld = worldManager.getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.findChunk(packetData.chunkX, packetData.chunkY);

        if (worldChunk == null) {
            // Generate new chunk and add it to the game world
            WorldChunk newChunk = new WorldChunk(packetData.chunkX, packetData.chunkY);
            newChunk.setLayers(packetData.layerDefinitionMap);
            gameWorld.setChunk(newChunk);
        } else {
            worldChunk.setLayers(packetData.layerDefinitionMap);
        }
    }

    @AllArgsConstructor
    class WorldChunkPacket extends PacketData {
        private final short chunkX, chunkY;
        private final Map<LayerDefinition, TileImage[]> layerDefinitionMap;
    }
}
