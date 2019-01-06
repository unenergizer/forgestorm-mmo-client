package com.valenguard.client.network.shared;

public class Opcodes {

    /**
     * SHARED
     */
    public static final byte PING = 0x00;
    public static final byte INVENTORY_UPDATE = 0x7D;
    public static final byte APPEARANCE = 0x7E;
    public static final byte CHAT = 0x7F;

    /**
     * SERVER -> CLIENT
     */
    public static final byte INIT_CLIENT_SESSION = 0x01;
    public static final byte ENTITY_SPAWN = 0x02;
    public static final byte ENTITY_DESPAWN = 0x03;
    public static final byte ENTITY_MOVE_UPDATE = 0x04;
    public static final byte INIT_MAP = 0x05;
    public static final byte EXPERIENCE = 0x06;

    /**
     * CLIENT -> SERVER
     */
    public static final byte MOVE_REQUEST = 0x01;
    public static final byte CLIENT_LOGIN = 0x02;
    public static final byte CLICK_ACTION = 0x03;
}
