package com.valenguard.client;

import com.valenguard.client.game.screens.ui.actors.constant.ScreenResolutions;

public class ClientConstants {
    public static final String GAME_VERSION = "0.1.0";
    public static final ScreenResolutions SCREEN_RESOLUTION = ScreenResolutions.DESKTOP_800_600;
    public static final float ZOOM_DEFAULT = 0.25f;
    public static final float ZOOM_CHANGE = 0.025f;
    public static final float ZOOM_LIMIT_OUT = 0.5f;
    public static final float ZOOM_LIMIT_IN = 0.025f;
    public static final short CLICK_RADIUS = 40;
    public static final String WEB_REGISTER = "http://valenguard.com/register/";
    public static final String WEB_LOST_PASSWORD = "http://valenguard.com/lost-password/";
    public static final short TILE_SIZE = 16;
    public static final String MAP_DIRECTORY = "data/maps";
    public static final float namePlateDistanceInPixels = 4;

    public static boolean MONITOR_MOVEMENT_CHECKS = false;

    public static final int BAG_WIDTH = 5;
    private static final int BAG_HEIGHT = 6;
    public static final int BAG_SIZE = BAG_WIDTH * BAG_HEIGHT;

    public static final int EQUIPMENT_INVENTORY_SIZE = 12;
}
