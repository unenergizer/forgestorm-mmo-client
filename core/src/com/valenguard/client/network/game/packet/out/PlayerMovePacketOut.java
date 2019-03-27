package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.network.game.shared.Opcodes;

import static com.valenguard.client.util.Preconditions.checkArgument;

public class PlayerMovePacketOut extends AbstractClientOutPacket {

    private final Location attemptLocation;

    public PlayerMovePacketOut(Location attemptLocation) {
        super(Opcodes.MOVE_REQUEST);
        this.attemptLocation = attemptLocation;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        checkArgument(!EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().equals(attemptLocation),
                "Locations can not be equal!");
        write.writeShort(attemptLocation.getX());
        write.writeShort(attemptLocation.getY());
    }
}
