package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;
import com.valenguard.client.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SendChatMessage extends ClientOutPacket {

    private final String chatMessage;

    public SendChatMessage(String chatMessage) {
        super(Opcodes.CHAT);
        this.chatMessage = chatMessage;
    }

    @Override
    protected void createPacket(ObjectOutputStream write) throws IOException {
        if (chatMessage == null) return;
        if (chatMessage.isEmpty()) return;
        if (chatMessage.contains(Character.toString('\n'))) return; // enter
        if (chatMessage.contains(Character.toString('\r'))) return; // tab


        Log.println(getClass(), "Characters before trimming: " + chatMessage);
        String newMessage = chatMessage.trim();
        Log.println(getClass(), "Characters after trimming: " + newMessage);
        if (newMessage.isEmpty()) return;

        Log.println(getClass(), "Sending message: " + newMessage + " with length: " + newMessage.length() + " across the wire");

        write.writeUTF(newMessage);
        Log.println(getClass(), chatMessage);
    }
}
