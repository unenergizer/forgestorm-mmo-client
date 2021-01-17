package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.network.game.shared.Opcodes;

public class TileWarpPacketOut extends AbstractClientPacketOut {

    private final int fromX, fromY;
    private final String toWorldName;
    private final int toX, toY;
    private final MoveDirection facingDirection;

    public TileWarpPacketOut(int fromX, int fromY, String toWorldName, int toX, int toY, MoveDirection facingDirection) {
        super(Opcodes.WORLD_CHUNK_WARP);
        this.fromX = fromX;
        this.fromY = fromY;
        this.toWorldName = toWorldName;
        this.toX = toX;
        this.toY = toY;
        this.facingDirection = facingDirection;
    }

    @Override
    void createPacket(ForgeStormOutputStream write) {
        write.writeInt(fromX);
        write.writeInt(fromY);
        write.writeString(toWorldName);
        write.writeInt(toX);
        write.writeInt(toY);
        write.writeByte(facingDirection.getDirectionByte());
    }
}
