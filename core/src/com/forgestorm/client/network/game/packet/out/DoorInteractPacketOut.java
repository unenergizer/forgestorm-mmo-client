package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.maps.DoorManager;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class DoorInteractPacketOut extends AbstractClientPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final DoorManager.DoorStatus doorStatus;
    private final int tileX, tileY;

    public DoorInteractPacketOut(DoorManager.DoorStatus doorStatus, int tileX, int tileY) {
        super(Opcodes.DOOR_INTERACT);
        this.doorStatus = doorStatus;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        write.writeByte(DoorManager.DoorStatus.getByte(doorStatus));
        write.writeInt(tileX);
        write.writeInt(tileY);

        println(getClass(), "DoorStatus: " + doorStatus.name(), false, PRINT_DEBUG);
        println(getClass(), "tileX: " + tileX, false, PRINT_DEBUG);
        println(getClass(), "tileY: " + tileY, false, PRINT_DEBUG);
    }
}
