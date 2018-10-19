package com.valenguard.client.network.packet.out;

import com.valenguard.client.constants.Direction;
import com.valenguard.client.network.shared.Opcodes;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class PlayerMove extends ClientOutPacket {

    private static final String TAG = PlayerMove.class.getSimpleName();

    private Direction direction;

    public PlayerMove(Direction direction) {
        super(Opcodes.MOVE_REQUEST);
        this.direction = direction;
    }

    @Override
    protected void createPacket(ObjectOutputStream write) throws IOException {
        write.writeByte(direction.getDirection());
    }
}
