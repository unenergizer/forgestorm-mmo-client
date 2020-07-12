package com.forgestorm.client.network.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("unused")
@AllArgsConstructor
public enum LoginFailReason {
    ACCOUNT_BANNED("[RED]Your account has been banned."),
    ALREADY_LOGGED_IN("[YELLOW]This account is already logged in."),
    INCORRECT_ACCOUNT_DETAILS("[YELLOW]Invalid username/password combination."),
    FAILED_TO_CONNECT("[YELLOW]Failed to connect to the server.");

    @Getter
    private String failReasonMessage;

    public static LoginFailReason getLoginFailReason(byte enumIndex) {
        for (LoginFailReason loginFailReason : LoginFailReason.values()) {
            if ((byte) loginFailReason.ordinal() == enumIndex) return loginFailReason;
        }
        throw new RuntimeException("LoginFailReason type miss match! Byte Received: " + enumIndex);
    }

    public static byte getByte(LoginFailReason loginFailReason) {
        return (byte) loginFailReason.ordinal();
    }
}
