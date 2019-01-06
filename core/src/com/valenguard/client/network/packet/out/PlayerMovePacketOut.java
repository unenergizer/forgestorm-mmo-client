package com.valenguard.client.network.packet.out;

import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.network.shared.Opcodes;

public class PlayerMovePacketOut extends ClientAbstractOutPacket {

    private final MoveDirection moveDirection;

    public PlayerMovePacketOut(MoveDirection moveDirection) {
        super(Opcodes.MOVE_REQUEST);
        this.moveDirection = moveDirection;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        if (moveDirection == MoveDirection.NONE) throw new RuntimeException("Move direction was none.");
//        println(getClass(), "Sending remove direction to server: " + moveDirection);
        write.writeByte(moveDirection.getDirectionByte());
    }
}
