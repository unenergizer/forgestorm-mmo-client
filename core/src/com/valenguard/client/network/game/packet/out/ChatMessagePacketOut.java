package com.valenguard.client.network.game.packet.out;

import com.valenguard.client.network.game.shared.Opcodes;

import static com.valenguard.client.util.Log.println;

public class ChatMessagePacketOut extends AbstractClientOutPacket {

    private final String chatMessage;

    public ChatMessagePacketOut(String chatMessage) {
        super(Opcodes.CHAT);
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

        write.writeString(newMessage);
        println(getClass(), chatMessage);
    }
}
