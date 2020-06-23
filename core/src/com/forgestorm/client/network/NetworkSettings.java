package com.forgestorm.client.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NetworkSettings {
    private String loginIp;
    private int loginPort;
    private String gameIp;
    private int gamePort;
}
