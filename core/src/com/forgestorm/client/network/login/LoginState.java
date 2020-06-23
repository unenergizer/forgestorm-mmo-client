package com.forgestorm.client.network.login;

import java.util.UUID;

import lombok.Getter;

@Getter
public class LoginState {

    private UUID uuid;
    private Boolean loginSuccess;
    private String failReason;

    LoginState failState(String failReason) {
        this.failReason = failReason;
        loginSuccess = false;
        return this;
    }

    LoginState successState(UUID uuid) {
        this.uuid = uuid;
        loginSuccess = true;
        return this;
    }

}
