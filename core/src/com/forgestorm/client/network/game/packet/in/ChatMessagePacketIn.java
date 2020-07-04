package com.forgestorm.client.network.game.packet.in;

import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.network.game.shared.ClientHandler;
import com.forgestorm.client.network.game.shared.Opcode;
import com.forgestorm.client.network.game.shared.Opcodes;
import com.forgestorm.client.network.game.shared.PacketData;
import com.forgestorm.client.network.game.shared.PacketListener;

import static com.forgestorm.client.util.Log.println;

@Opcode(getOpcode = Opcodes.CHAT)
public class ChatMessagePacketIn implements PacketListener<ChatMessagePacketIn.ChatMessagePacket> {

    private final static boolean PRINT_DEBUG = true;

    @Override
    public PacketData decodePacket(ClientHandler clientHandler) {
        return new ChatMessagePacket(clientHandler.readByte(), clientHandler.readString());
    }

    @Override
    public void onEvent(ChatMessagePacket packetData) {
        println(getClass(), "Channel: " + packetData.chatChannelType + ", Message: " + packetData.chatMessage, false, PRINT_DEBUG);
        ActorUtil.getStageHandler().getChatWindow().appendChatMessage(packetData.chatChannelType, packetData.chatMessage);
    }

    class ChatMessagePacket extends PacketData {
        private ChatChannelType chatChannelType;
        private String chatMessage;

        ChatMessagePacket(byte enumIndex, String chatMessage) {
            chatChannelType = ChatChannelType.getChannelType(enumIndex);
            this.chatMessage = chatMessage;
        }
    }
}
