package com.forgestorm.client.network.game.packet.out;

import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.network.game.shared.Opcodes;

import java.util.ArrayList;
import java.util.List;

import static com.forgestorm.client.util.Log.println;

public class ChatMessagePacketOut extends AbstractClientPacketOut {

    private static final boolean PRINT_DEBUG = false;

    private final ChatChannelType chatChannelType;
    private final String message;

    public ChatMessagePacketOut(ChatChannelType chatChannelType, String message) {
        super(Opcodes.CHAT);
        this.chatChannelType = chatChannelType;
        this.message = message.trim();
    }

    @Override
    protected void createPacket(ForgeStormOutputStream write) {
        if (message == null) return;
        if (message.isEmpty()) return;
        if (message.contains(Character.toString('\n'))) return; // enter
        if (message.contains(Character.toString('\r'))) return; // tab

        List<String> stringList = new ArrayList<String>();
        int index = 0;
        while (index < message.length()) {
            stringList.add(message.substring(index, Math.min(index + ClientConstants.MAX_CHAT_LENGTH, message.length())));
            index += ClientConstants.MAX_CHAT_LENGTH;
        }

        println(getClass(), "Chat Channel: " + chatChannelType.name(), false, PRINT_DEBUG);
        println(getClass(), "Message Count: " + stringList.size(), false, PRINT_DEBUG);

        write.writeByte(ChatChannelType.getByte(chatChannelType));
        write.writeByte((byte) stringList.size());

        for (String string : stringList) {
            write.writeString(string);
            println(getClass(), "String Wrote: " + string, false, PRINT_DEBUG);
        }
    }
}
