package com.forgestorm.client.network.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LoginFailReason {
    ACCOUNT_BANNED("This account has been banned."),
    ALREADY_LOGGED_IN("This account is already logged in."),
    INCORRECT_ACCOUNT_DETAILS("Wrong username/password combination."),
    FAILED_TO_CONNECT("Failed to connect to the server.");

    @Getter
    private String failReasonMessage;

    public static LoginFailReason getLoginFailReason(byte enumIndex) {
        for (LoginFailReason loginFailReason : LoginFailReason.values()) {
            if ((byte) loginFailReason.ordinal() == enumIndex) return loginFailReason;
        }
        throw new RuntimeException("LoginFailReason type miss match! Byte Received: " + enumIndex);
    }
}
