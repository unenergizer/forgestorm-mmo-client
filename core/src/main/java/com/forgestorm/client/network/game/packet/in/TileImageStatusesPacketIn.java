package com.forgestorm.client.network.game.packet.in;

import static com.forgestorm.client.util.Log.println;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.DoorManager;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Opcode(getOpcode = Opcodes.TILE_IMAGE_STATUSES)
public class TileImageStatusesPacketIn implements PacketListener<TileImageStatusesPacketIn.TileImageStatusesPacket> {

    private final static boolean PRINT_DEBUG = true;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        int numberOfStatuses = clientHandler.readInt();

        List<TileStatusData> tilesWithStatuses = new ArrayList<>();
        for (int i = 0; i < numberOfStatuses; i++) {
            int x = clientHandler.readInt();
            int y = clientHandler.readInt();
            short z = clientHandler.readShort();
            byte status = clientHandler.readByte();

            tilesWithStatuses.add(new TileStatusData(x, y, z, DoorManager.DoorStatus.getDoorStatus(status)));
        }

        return new TileImageStatusesPacket(tilesWithStatuses);
    }

    @Override
    public void onEvent(TileImageStatusesPacket packetData) {
        for (TileStatusData tileStatusData : packetData.tilesWithStatuses) {
            println(getClass(), "TileX: " + tileStatusData.tileX, false, PRINT_DEBUG);
            println(getClass(), "TileY: " + tileStatusData.tileY, false, PRINT_DEBUG);
            println(getClass(), "TileZ: " + tileStatusData.worldZ, false, PRINT_DEBUG);
            println(getClass(), "DoorStatus: " + tileStatusData.doorStatus, false, PRINT_DEBUG);

            ClientMain.getInstance().getDoorManager().networkToggleDoor(tileStatusData.doorStatus, tileStatusData.tileX, tileStatusData.tileY, tileStatusData.worldZ, false);
        }
    }

    @AllArgsConstructor
    class TileImageStatusesPacket extends PacketData {
        private final List<TileStatusData> tilesWithStatuses;
    }

    @Getter
    @AllArgsConstructor
    static class TileStatusData {
        int tileX;
        int tileY;
        short worldZ;
        DoorManager.DoorStatus doorStatus;
    }
}
