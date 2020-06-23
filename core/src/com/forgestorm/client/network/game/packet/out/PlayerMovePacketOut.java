package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Preconditions.checkArgument;

public class PlayerMovePacketOut extends AbstractClientPacketOut {

    private final Location attemptLocation;

    public PlayerMovePacketOut(Location attemptLocation) {
        super(Opcodes.MOVE_REQUEST);
        this.attemptLocation = new Location(attemptLocation);
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        checkArgument(!EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().equals(attemptLocation),
                "Locations can not be equal!");
        write.writeShort(attemptLocation.getX());
        write.writeShort(attemptLocation.getY());
    }
}
