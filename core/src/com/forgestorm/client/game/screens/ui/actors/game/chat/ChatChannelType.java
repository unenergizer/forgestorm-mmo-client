package com.forgestorm.client.game.screens.ui.actors.game.chat;

public enum ChatChannelType {
    GENERAL,
    COMBAT,
    TRADE;

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
