package com.valenguard.client.network.packet.in;

import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketListener;

public class EntityDespawn implements PacketListener {

    private static final String TAG = EntityDespawn.class.getSimpleName();

    @Opcode(getOpcode = Opcodes.ENTITY_EXIT_MAP)
    public void onEntityExitMap(ClientHandler clientHandler) {

    }
}
