package com.forgestorm.shared.game.world.entities;

@SuppressWarnings("unused")
public enum FirstInteraction {
    TALK,
    SHOP,
    BANK,
    ATTACK;

    public static FirstInteraction getFirstInteraction(byte enumIndex) {
        for (FirstInteraction firstInteraction : FirstInteraction.values()) {
            if ((byte) firstInteraction.ordinal() == enumIndex) return firstInteraction;
        }
        throw new RuntimeException("LoginFailReason type miss match! Byte Received: " + enumIndex);
    }

    public static byte getByte(FirstInteraction firstInteraction) {
        return (byte) firstInteraction.ordinal();
    }
}
