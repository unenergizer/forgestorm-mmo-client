package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Preconditions.checkArgument;

public class PlayerMovePacketOut extends AbstractClientPacketOut {

    private final String mapName;
    private final short x;
    private final short y;

    public PlayerMovePacketOut(Location attemptLocation) {
        super(Opcodes.MOVE_REQUEST);
        mapName = attemptLocation.getMapName();
        x = attemptLocation.getX();
        y = attemptLocation.getY();
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        checkArgument(!EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().equals(new Location(mapName, x, y)),
                "Locations can not be equal!");
        write.writeShort(x);
        write.writeShort(y);
    }
}
