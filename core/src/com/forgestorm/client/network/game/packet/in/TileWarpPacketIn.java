package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.GameWorld;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.WorldChunk;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.game.world.maps.MoveDirection;
import com.forgestorm.shared.game.world.maps.Warp;
import com.forgestorm.shared.game.world.maps.WarpLocation;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.WORLD_CHUNK_WARP)
public class TileWarpPacketIn implements PacketListener<TileWarpPacketIn.ChunkWarpDataPacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        boolean clearWarps = clientHandler.readBoolean();

        int fromX = clientHandler.readInt();
        int fromY = clientHandler.readInt();
        short fromZ = clientHandler.readShort();

        String worldName = clientHandler.readString();
        int toX = clientHandler.readInt();
        int toY = clientHandler.readInt();
        short toZ = clientHandler.readShort();
        byte facingDirection = clientHandler.readByte();
        Warp warp = new Warp(new Location(worldName, toX, toY, toZ), MoveDirection.getDirection(facingDirection));

        return new ChunkWarpDataPacket(clearWarps, new WarpLocation(fromX, fromY, fromZ), warp);
    }

    @Override
    public void onEvent(ChunkWarpDataPacket packetData) {
        Location warpLocation = packetData.warp.getWarpDestination();
        GameWorld gameWorld = ClientMain.getInstance().getWorldManager().getCurrentGameWorld();
        WorldChunk worldChunk = gameWorld.findChunk(warpLocation.getX(), warpLocation.getY());

        if (packetData.clearWarps) worldChunk.clearTileWarps();

        // Need to set the location of the warp....
        worldChunk.addTileWarp(packetData.warpDestination, packetData.warp);
    }

    @AllArgsConstructor
    static class ChunkWarpDataPacket extends PacketData {
        private final boolean clearWarps;
        private final WarpLocation warpDestination;
        private final Warp warp;
    }
}
