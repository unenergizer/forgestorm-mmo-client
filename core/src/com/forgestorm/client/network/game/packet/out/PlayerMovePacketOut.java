package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Preconditions.checkArgument;

public class PlayerMovePacketOut extends AbstractClientPacketOut {

    private final String worldName;
    private final int x;
    private final int y;

    public PlayerMovePacketOut(Location attemptLocation) {
        super(Opcodes.MOVE_REQUEST);
        worldName = attemptLocation.getWorldName();
        x = attemptLocation.getX();
        y = attemptLocation.getY();
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        checkArgument(!EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().equals(new Location(worldName, x, y)),
                "Locations can not be equal!");
        write.writeInt(x);
        write.writeInt(y);
    }
}
