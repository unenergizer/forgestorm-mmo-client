package com.valenguard.client.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PlayerSession {
    private final String username;
    private final String password;
}
