package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;
import com.valenguard.client.util.Log;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.CHAT)
public class ReceiveChatMessage implements PacketListener<ReceiveChatMessage.ChatMessagePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new ChatMessagePacket(clientHandler.readString());
    }

    @Override
    public void onEvent(ChatMessagePacket packetData) {
        Log.println(getClass(), packetData.chatMessage);
        Valenguard.getInstance().getStageHandler().getChatWindow().appendChatMessage(packetData.chatMessage);
    }

    @AllArgsConstructor
    class ChatMessagePacket extends PacketData {
        private String chatMessage;
    }
}
