package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.network.game.GameOutputStream;
import lombok.Getter;

public abstract class AbstractPacketOut {

    private ClientMain clientMain;

    /**
     * Opcode to send with the out-going packet.
     */
    @Getter
    private final byte opcode;

    AbstractPacketOut(ClientMain clientMain, byte opcode) {
        this.clientMain = clientMain;
        this.opcode = opcode;
    }

    /**
     * Sends the packet to the player.
     */
    public void sendPacket() {
        clientMain.getConnectionManager().getOutputStreamManager().addClientOutPacket(this);
    }

    /**
     * Creates the packet.
     */
    public abstract void createPacket(GameOutputStream write);
}
