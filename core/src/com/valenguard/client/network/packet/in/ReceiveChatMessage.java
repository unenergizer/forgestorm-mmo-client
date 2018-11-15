package com.valenguard.client.network.packet.in;

import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.stage.game.ChatBox;
import com.valenguard.client.network.shared.ClientHandler;
import com.valenguard.client.network.shared.Opcode;
import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.network.shared.PacketData;
import com.valenguard.client.network.shared.PacketListener;

import lombok.AllArgsConstructor;

@Opcode(getOpcode = Opcodes.CHAT)
public class ReceiveChatMessage implements PacketListener<ReceiveChatMessage.ChatMessagePacket> {

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new ChatMessagePacket(clientHandler.readString());
    }

    @Override
    public void onEvent(ChatMessagePacket packetData) {
        ((ChatBox) Valenguard.getInstance().getUiManager().getAbstractUI("chatbox")).updateChatBox(packetData.chatMessage);
    }

    @AllArgsConstructor
    class ChatMessagePacket extends PacketData {
        private String chatMessage;
    }
}
