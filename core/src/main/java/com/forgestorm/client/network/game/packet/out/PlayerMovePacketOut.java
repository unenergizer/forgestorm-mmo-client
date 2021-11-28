package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

import static com.forgestorm.client.util.Preconditions.checkArgument;

public class PlayerMovePacketOut extends AbstractPacketOut {

    private final String worldName;
    private final int x;
    private final int y;
    private final short z;

    public PlayerMovePacketOut(Location attemptLocation) {
        super(Opcodes.MOVE_REQUEST);
        worldName = attemptLocation.getWorldName();
        x = attemptLocation.getX();
        y = attemptLocation.getY();
        z = attemptLocation.getZ();
    }

    @Override
    public void createPacket(GameOutputStream write) {
        checkArgument(!EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().equals(new Location(worldName, x, y, z)),
                "Locations can not be equal!");
        write.writeInt(x);
        write.writeInt(y);
        write.writeShort(z);
    }
}
