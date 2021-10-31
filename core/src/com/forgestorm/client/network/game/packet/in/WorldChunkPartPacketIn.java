package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.game.world.maps.WorldManager;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.WORLD_CHUNK_LAYER)
public class WorldChunkPartPacketIn implements PacketListener<WorldChunkPartPacketIn.WorldChunkPartPacket> {

    private static final boolean PRINT_DEBUG = false;

    private final WorldManager worldManager = ClientMain.getInstance().getWorldManager();

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {

        // Read chunk location
        short chunkX = clientHandler.readShort();
        short chunkY = clientHandler.readShort();

        // Get layer information
        short floor = clientHandler.readShort();
        LayerDefinition layerDefinition = LayerDefinition.getLayerDefinition(clientHandler.readByte());
        byte sectionSent = clientHandler.readByte();

        // Read layer part
        int[] layerParts = new int[ClientConstants.MAX_TILE_GET];

        // Read TileImage ID's
        for (int i = 0; i < ClientConstants.MAX_TILE_GET; i++) {
            layerParts[i] = clientHandler.readInt();
        }

        println(getClass(), "Receiving chunk section! Layer: " + layerDefinition + ", Section: " + sectionSent, true, PRINT_DEBUG);

        return new WorldChunkPartPacket(chunkX, chunkY, Floors.getFloor(floor), layerDefinition, sectionSent, layerParts);
    }

    @Override
    public void onEvent(WorldChunkPartPacket packetData) {
        GameWorld gameWorld = worldManager.getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.getChunk(packetData.chunkX, packetData.chunkY);
        worldChunk.setNetworkTiles(packetData.floor, packetData.layerDefinition, packetData.sectionSent, packetData.layerPart);
    }

    @AllArgsConstructor
    class WorldChunkPartPacket extends PacketData {
        private final short chunkX, chunkY;
        private final Floors floor;
        private final LayerDefinition layerDefinition;
        private final byte sectionSent;
        private final int[] layerPart;
    }
}
