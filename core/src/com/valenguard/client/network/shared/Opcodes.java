package com.valenguard.client.network.shared;

public class Opcodes {

    /**
     * Don't forget to register your network listeners!
     */

    public static final byte INIT_PLAYER_CLIENT = 0x00;
    public static final byte PING = 0x01;
    public static final byte ENTITY_SPAWN = 0x02;
    public static final byte ENTITY_DESPAWN = 0x03;


    // ONLY ADDED TO REMOVE ERRORS. DELETE THESE!
    public static final byte MOVE_REQUEST = 0x04;
    public static final byte ENTITY_MOVE_UPDATE = 0x05;
}
