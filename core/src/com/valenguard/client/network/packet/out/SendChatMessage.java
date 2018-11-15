package com.valenguard.client.network.packet.out;

import com.valenguard.client.network.shared.Opcodes;

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
        if (chatMessage.isEmpty()) return;
        write.writeUTF(chatMessage);
    }
}
