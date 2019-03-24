package com.valenguard.client.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class NetworkSettings {
    private String loginIp;
    private int loginPort;
    private String gameIp;
    private int gamePort;
}
