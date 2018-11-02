package com.valenguard.client.network.packet.out;

import com.valenguard.client.entities.MoveDirection;
import com.valenguard.client.network.shared.Opcodes;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class PlayerMove extends ClientOutPacket {

    private final MoveDirection moveDirection;

    public PlayerMove(MoveDirection moveDirection) {
        super(Opcodes.MOVE_REQUEST);
        this.moveDirection = moveDirection;
    }

    @Override
    protected void createPacket(ObjectOutputStream write) throws IOException {
        if (moveDirection == MoveDirection.NONE) throw new RuntimeException("Move direction was none.");
//        System.out.println("Sending remove direction to server: " + moveDirection);
        write.writeByte(moveDirection.getDirectionByte());
    }
}
