package com.valenguard.client.network.game.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.dev.DevMenu;
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
        return new ClientPrivilegePacket(isAdmin);
    }

    @Override
    public void onEvent(ClientPrivilegePacket packetData) {
        if (packetData.isAdmin) {
            Valenguard.getInstance().setAdmin(packetData.isAdmin);
            ActorUtil.getStageHandler().getStage().addActor(new DevMenu().build());
        }
    }

    @AllArgsConstructor
    class ClientPrivilegePacket extends PacketData {
        boolean isAdmin;
    }
}
