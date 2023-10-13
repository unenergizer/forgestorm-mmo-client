package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.shared.network.game.GameOutputStream;
import com.forgestorm.shared.network.game.Opcodes;

import static com.forgestorm.client.util.Preconditions.checkArgument;

public class PlayerMovePacketOut extends AbstractPacketOut {

    private final ClientMain clientMain;
    private final String worldName;
    private final int x;
    private final int y;
    private final short z;

    public PlayerMovePacketOut(ClientMain clientMain, Location attemptLocation) {
        super(clientMain, Opcodes.MOVE_REQUEST);
        this.clientMain = clientMain;
        worldName = attemptLocation.getWorldName();
        x = attemptLocation.getX();
        y = attemptLocation.getY();
        z = attemptLocation.getZ();
    }

    @Override
    public void createPacket(GameOutputStream write) {
        checkArgument(!clientMain.getEntityManager().getPlayerClient().getCurrentMapLocation().equals(new Location(clientMain, worldName, x, y, z)),
                "Locations can not be equal!");
        write.writeInt(x);
        write.writeInt(y);
        write.writeShort(z);
    }
}
