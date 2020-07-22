package com.forgestorm.client.network.game.shared;

public class Opcodes {

    /**
     * SHARED
     */
    public static final byte PING = 0x00;
    public static final byte PROFILE_REQUEST = 0x79;
    public static final byte INSPECT_PLAYER = 0x7A;
    public static final byte BANK_MANAGEMENT = 0x7B;
    public static final byte PLAYER_TRADE = 0x7C;
    public static final byte INVENTORY_UPDATE = 0x7D;
    public static final byte APPEARANCE = 0x7E;
    public static final byte CHAT = 0x7F;

    /**
     * SERVER -> CLIENT
     */
    public static final byte INIT_SCREEN = 0x01;
    public static final byte ENTITY_SPAWN = 0x02;
    public static final byte ENTITY_DESPAWN = 0x03;
    public static final byte ENTITY_MOVE_UPDATE = 0x04;
    public static final byte INIT_MAP = 0x05;
    public static final byte EXPERIENCE = 0x06;
    public static final byte ATTRIBUTES_UPDATE = 0x07;
    public static final byte PLAYER_TELEPORT = 0x08;
    public static final byte ENTITY_DAMAGE_OUT = 0x09;
    public static final byte ENTITY_HEAL_OUT = 0x0A;
    public static final byte AI_ENTITY_UPDATE_OUT = 0x0B;
    public static final byte CHARACTERS_MENU_LOAD = 0x0C;
    public static final byte ENTITY_UPDATE_SPEED = 0x0D;
    public static final byte INIT_CLIENT_PRIVILEGE = 0x0E;
    public static final byte CLIENT_MOVE_RESYNC = 0x0F;
    public static final byte CHARACTER_CREATOR_ERROR = 0x10;

    /**
     * CLIENT -> SERVER
     */
    public static final byte MOVE_REQUEST = 0x01;
    public static final byte CLICK_ACTION = 0x02;
    public static final byte ENTITY_SHOPS = 0x03;
    public static final byte CHARACTER_SELECT = 0x04;
    public static final byte CHARACTER_CREATOR = 0x05;
    public static final byte CHARACTER_DELETE = 0x06;
    public static final byte CHARACTER_LOGOUT = 0x07;
    public static final byte ABILITY_REQUEST = 0x08;
    public static final byte ADMIN_EDITOR_ENTITY = 0x09;

}
