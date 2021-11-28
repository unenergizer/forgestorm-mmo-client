package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.network.game.GameOutputStream;

import lombok.Getter;

public abstract class AbstractPacketOut {

    /**
     * Opcode to send with the out-going packet.
     */
    @Getter
    private final byte opcode;

    AbstractPacketOut(byte opcode) {
        this.opcode = opcode;
    }

    /**
     * Sends the packet to the player.
     */
    public void sendPacket() {
        ClientMain.getInstance().getConnectionManager().getOutputStreamManager().addClientOutPacket(this);
    }

    /**
     * Creates the packet.
     */
    public abstract void createPacket(GameOutputStream write);
}
