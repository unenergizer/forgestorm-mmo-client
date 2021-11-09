package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.profile.SecondaryUserGroups;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.INIT_CLIENT_PRIVILEGE)
public class InitClientPrivilegePacketIn implements PacketListener<InitClientPrivilegePacketIn.ClientPrivilegePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        // Get secondary permissions
        List<Byte> secondaryGroupIds = new ArrayList<Byte>();
        byte arraySize = clientHandler.readByte();

        for (byte b = 0; b < arraySize; b++) {
            secondaryGroupIds.add(clientHandler.readByte());
        }

        // Get explicit permissions
        boolean isAdmin = clientHandler.readBoolean();
        boolean isMod = clientHandler.readBoolean();
        return new ClientPrivilegePacket(secondaryGroupIds, isAdmin, isMod);
    }

    @Override
    public void onEvent(ClientPrivilegePacket packetData) {
        ClientMain clientMain = ClientMain.getInstance();
        clientMain.setContentDeveloper(isContentDeveloper(packetData.secondaryGroupIds));
        clientMain.setAdmin(packetData.isAdmin);
        clientMain.setModerator(packetData.isMod);

        // Setup staff chat channel, if applicable...
        if (packetData.isAdmin || packetData.isMod) {
            clientMain.getStageHandler().getChatWindow().addChatChannel(ChatChannelType.STAFF);
        }
    }

    private boolean isContentDeveloper(List<Byte> secondaryGroupIds) {
        for (Byte b : secondaryGroupIds) {
            if (b == SecondaryUserGroups.CONTENT_DEVELOPER.getUserGroupId()) return true;
        }
        return false;
    }

    @AllArgsConstructor
    class ClientPrivilegePacket extends PacketData {
        List<Byte> secondaryGroupIds;
        boolean isAdmin;
        boolean isMod;
    }
}
