package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

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
        ClientMain clientMain = ClientMain.getInstance();
        clientMain.setAdmin(packetData.isAdmin);
        clientMain.setModerator(packetData.isMod);

        // Setup staff chat channel, if applicable...
        if (packetData.isAdmin || packetData.isMod) {
            clientMain.getStageHandler().getChatWindow().addChatChannel(ChatChannelType.STAFF);
        }
    }

    @AllArgsConstructor
    class ClientPrivilegePacket extends PacketData {
        boolean isAdmin;
        boolean isMod;
    }
}
