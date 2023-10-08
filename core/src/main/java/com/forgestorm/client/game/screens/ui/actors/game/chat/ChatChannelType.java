package com.forgestorm.client.game.screens.ui.actors.game.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChatChannelType {
    GENERAL(true),
    COMBAT(false),
    TRADE(true),
    STAFF(true);

    @Getter
    private final boolean canSendMessages;

    public static ChatChannelType getChannelType(byte enumIndex) {
        for (ChatChannelType chatChannelType : ChatChannelType.values()) {
            if ((byte) chatChannelType.ordinal() == enumIndex) return chatChannelType;
        }
        throw new RuntimeException("ChatChannel type miss match! Byte Received: " + enumIndex);
    }

    public static byte getByte(ChatChannelType chatChannelType) {
        return (byte) chatChannelType.ordinal();
    }
}
