package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.network.game.shared.Opcodes;

import static com.forgestorm.client.util.Log.println;

public class ChatMessagePacketOut extends AbstractClientPacketOut {

    private final ChatChannelType chatChannelType;
    private final String chatMessage;

    public ChatMessagePacketOut(ChatChannelType chatChannelType, String chatMessage) {
        super(Opcodes.CHAT);
        this.chatChannelType = chatChannelType;
        this.chatMessage = chatMessage;
    }

    @Override
    protected void createPacket(ValenguardOutputStream write) {
        if (chatMessage == null) return;
        if (chatMessage.isEmpty()) return;
        if (chatMessage.contains(Character.toString('\n'))) return; // enter
        if (chatMessage.contains(Character.toString('\r'))) return; // tab


        println(getClass(), "Characters before trimming: " + chatMessage);
        String newMessage = chatMessage.trim();
        println(getClass(), "Characters after trimming: " + newMessage);
        if (newMessage.isEmpty()) return;

        println(getClass(), "Sending message: " + newMessage + " with length: " + newMessage.length() + " across the wire");

        write.writeByte(ChatChannelType.getByte(chatChannelType));
        write.writeString(newMessage);
        println(getClass(), chatMessage);
    }
}
