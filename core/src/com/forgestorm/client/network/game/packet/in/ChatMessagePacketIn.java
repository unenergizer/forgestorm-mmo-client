package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.CHAT)
public class ChatMessagePacketIn implements PacketListener<ChatMessagePacketIn.ChatMessagePacket> {

    private final static boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new ChatMessagePacket(clientHandler.readString());
    }

    @Override
    public void onEvent(ChatMessagePacket packetData) {
        println(getClass(), packetData.chatMessage, false, PRINT_DEBUG);
        ActorUtil.getStageHandler().getChatWindow().appendChatMessage(packetData.chatMessage);
    }

    @AllArgsConstructor
    class ChatMessagePacket extends PacketData {
        private String chatMessage;
    }
}
