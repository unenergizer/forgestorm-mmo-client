package com.valenguard.client.network.shared;

public class Opcodes {

    /**
     * SHARED
     */
    public static final byte PING = 0x00;

    /**
     * SERVER -> CLIENT
     */
    public static final byte INIT_CLIENT_SESSION = 0x01;
    public static final byte ENTITY_SPAWN = 0x02;
    public static final byte ENTITY_DESPAWN = 0x03;
    public static final byte ENTITY_MOVE_UPDATE = 0x04;
    public static final byte INIT_MAP = 0x05;

    /**
     * CLIENT -> SERVER
     */
    public static final byte MOVE_REQUEST = 0x01;
    public static final byte CLIENT_LOGIN = 0x02;
}
