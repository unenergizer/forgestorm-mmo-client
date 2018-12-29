package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.valenguard.client.util.Log.println;

@Opcode(getOpcode = Opcodes.CHAT)
public class ChatMessagePacketIn implements PacketListener<ChatMessagePacketIn.ChatMessagePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new ChatMessagePacket(clientHandler.readString());
    }

    @Override
    public void onEvent(ChatMessagePacket packetData) {
        println(getClass(), packetData.chatMessage);
        Valenguard.getInstance().getStageHandler().getChatWindow().appendChatMessage(packetData.chatMessage);
    }

    @AllArgsConstructor
    class ChatMessagePacket extends PacketData {
        private String chatMessage;
    }
}
