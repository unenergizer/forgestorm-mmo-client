package com.forgestorm.client;

import com.forgestorm.client.game.screens.ui.actors.constant.ScreenResolutions;

public class ClientConstants {
    public static final ScreenResolutions SCREEN_RESOLUTION = ScreenResolutions.DESKTOP_1024_600;
    public static final float ZOOM_DEFAULT = 0.25f;
    public static final float ZOOM_CHANGE = 0.025f;
    public static final float ZOOM_LIMIT_OUT = 0.5f;
    public static final float ZOOM_LIMIT_IN = 0.025f;
    public static final short CLICK_RADIUS = 40;
    public static final String WEB_REGISTER = "https://forgestorm.com/register/";
    public static final String WEB_LOST_PASSWORD = "https://forgestorm.com/lost-password/";
    public static final short TILE_SIZE = 16;
    public static final float namePlateDistanceInPixels = 6;

    public static final short MAX_INTERACT_DISTANCE = 7;
    public static final byte MAX_CHAT_LENGTH = 127; // Max chat length is 0x7F.

    public static boolean MONITOR_MOVEMENT_CHECKS = false;

    public static final int EQUIPMENT_INVENTORY_SIZE = 13;

    public static final int NETWORK_SECONDS_TO_TIMEOUT = 10;

    public static final int MAX_PREVIOUS_SCROLL_MESSAGES = 10;

    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_RADIUS = 1;
    public static final int MAX_TILE_GET = 4 * 4;

    public static final int STARTER_GEAR_CHEST_ID = 2;
    public static final int STARTER_GEAR_PANTS_ID = 23;
    public static final int STARTER_GEAR_SHOES_ID = 7;
}
