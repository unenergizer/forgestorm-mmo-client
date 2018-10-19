package com.valenguard.client.network;

import com.valenguard.client.network.packet.out.PingOut;

import lombok.Getter;

public class PingManager {

    @Getter
    private volatile long latency;
    private long pingOutTime;

    private void pingOut() {
        pingOutTime = System.currentTimeMillis();
        new PingOut().sendPacket();
    }

    public void pingIn() {
        latency = System.currentTimeMillis() - pingOutTime;
        pingOut(); // Send next ping
    }
}
