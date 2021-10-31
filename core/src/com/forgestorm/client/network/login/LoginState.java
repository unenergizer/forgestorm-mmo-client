package com.forgestorm.client.network.login;

import com.forgestorm.shared.network.login.LoginFailReason;

import java.util.UUID;

import lombok.Getter;

@Getter
public class LoginState {

    private UUID uuid;
    private Boolean loginSuccess;
    private com.forgestorm.shared.network.login.LoginFailReason loginFailReason;

    LoginState failState(LoginFailReason loginFailReason) {
        this.loginFailReason = loginFailReason;
        loginSuccess = false;
        return this;
    }

    void successState(UUID uuid) {
        this.uuid = uuid;
        loginSuccess = true;
    }

}
