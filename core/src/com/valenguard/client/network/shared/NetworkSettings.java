package com.valenguard.client.network.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NetworkSettings {
    private String ip;
    private int port;
}
