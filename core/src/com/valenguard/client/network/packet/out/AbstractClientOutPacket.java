package com.valenguard.client.network.packet.out;

import com.valenguard.client.Valenguard;

import lombok.Getter;

public abstract class AbstractClientOutPacket {

    /**
     * Opcode to send with the out-going packet.
     */
    @Getter
    private final byte opcode;

    AbstractClientOutPacket(byte opcode) {
        this.opcode = opcode;
    }

    /**
     * Sends the packet to the player.
     */
    public void sendPacket() {
        Valenguard.getInstance().getOutputStreamManager().addClientOutPacket(this);
    }

    /**
     * Creates the packet.
     */
    abstract void createPacket(ValenguardOutputStream write);
}
