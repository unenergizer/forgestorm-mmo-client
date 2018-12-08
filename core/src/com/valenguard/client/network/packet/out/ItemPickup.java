package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ItemPickup extends ClientOutPacket {

    private int pickupX, pickupY;

    public ItemPickup(int pickupX, int pickupY) {
        super(Opcodes.CLIENT_LOGIN);
        this.pickupX = pickupX;
        this.pickupY = pickupY;
    }

    @Override
    protected void createPacket(DataOutputStream write) throws IOException {
        write.writeInt(pickupX);
        write.writeInt(pickupY);
    }
}
