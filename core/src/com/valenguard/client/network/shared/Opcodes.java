package com.valenguard.client.network.shared;

public class Opcodes {

    /**
     * Don't forget to register your network listeners!
     */

    public static final byte INIT_PLAYER_CLIENT = 0x00;
    public static final byte PING = 0x01;
    public static final byte SPAWN_ENTITY = 0x02;




    // ONLY ADDED TO REMOVE ERRORS. DELETE THESE!
    public static final byte MOVE_REQUEST = 0x03;
    public static final byte MOVE_REPLY = 0x03;
    public static final byte ENTITY_MOVE_UPDATE = 0x03;
    public static final byte ENTITY_EXIT_MAP = 0x03;
    public static final byte ENTITY_JOINED_MAP = 0x03;
    public static final byte PLAYER_MAP_CHANGE = 0x03;
}
