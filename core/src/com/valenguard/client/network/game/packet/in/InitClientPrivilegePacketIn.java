package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.game.shared.ClientHandler;
import com.valenguard.client.network.game.shared.Opcode;
import com.valenguard.client.network.game.shared.Opcodes;
import com.valenguard.client.network.game.shared.PacketData;
import com.valenguard.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.INIT_CLIENT_PRIVILEGE)
public class InitClientPrivilegePacketIn implements PacketListener<InitClientPrivilegePacketIn.ClientPrivilegePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        boolean isAdmin = clientHandler.readBoolean();
        boolean isMod = clientHandler.readBoolean();
        return new ClientPrivilegePacket(isAdmin, isMod);
    }

    @Override
    public void onEvent(ClientPrivilegePacket packetData) {
        Valenguard valenguard = Valenguard.getInstance();
        valenguard.setAdmin(packetData.isAdmin);
        valenguard.setModerator(packetData.isMod);
    }

    @AllArgsConstructor
    class ClientPrivilegePacket extends PacketData {
        boolean isAdmin;
        boolean isMod;
    }
}
