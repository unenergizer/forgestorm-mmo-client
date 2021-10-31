package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;
import com.forgestorm.shared.network.game.Opcode;
import com.forgestorm.shared.network.game.Opcodes;

import lombok.AllArgsConstructor;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.CHAT)
public class ChatMessagePacketIn implements PacketListener<ChatMessagePacketIn.ChatMessagePacket> {

    private final static boolean PRINT_DEBUG = false;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        ChatChannelType chatChannelType = ChatChannelType.getChannelType(clientHandler.readByte());
        byte messageCount = clientHandler.readByte();
        StringBuilder message = new StringBuilder();

        println(getClass(), "Chat Channel: " + chatChannelType.name(), false, PRINT_DEBUG);
        println(getClass(), "Message Count: " + messageCount, false, PRINT_DEBUG);

        for (byte i = 0; i < messageCount; i++) {
            String string = clientHandler.readString();
            message.append(string);
            println(getClass(), "String Read: " + string, false, PRINT_DEBUG);
        }

        return new ChatMessagePacket(chatChannelType, message.toString());
    }

    @Override
    public void onEvent(ChatMessagePacket packetData) {
        println(getClass(), "Channel: " + packetData.chatChannelType + ", Message: " + packetData.chatMessage, false, PRINT_DEBUG);
        ActorUtil.getStageHandler().getChatWindow().appendChatMessage(packetData.chatChannelType, packetData.chatMessage);
    }

    @AllArgsConstructor
    class ChatMessagePacket extends PacketData {
        private final ChatChannelType chatChannelType;
        private final String chatMessage;
    }
}
