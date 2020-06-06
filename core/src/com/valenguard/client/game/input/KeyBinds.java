package com.valenguard.client.game.input;

import com.badlogic.gdx.Input;

public class KeyBinds {

    /*
     * MOVEMENT
     */
    public static final int UP = Input.Keys.W;
    public static final int DOWN = Input.Keys.S;
    public static final int LEFT = Input.Keys.A;
    public static final int RIGHT = Input.Keys.D;

    public static final int UP_ALT = Input.Keys.UP;
    public static final int DOWN_ALT = Input.Keys.DOWN;
    public static final int LEFT_ALT = Input.Keys.LEFT;
    public static final int RIGHT_ALT = Input.Keys.RIGHT;

    /*
     * DEBUG
     */
    public static final int GAME_DEBUG = Input.Keys.F3;
    public static final int SCENE2D_DEBUG = Input.Keys.F11;

    /*
     * CHAT
     */
    public static final int CHAT_BOX_FOCUS = Input.Keys.ENTER;

    /*
     * WINDOWS
     */
    public static final int ESCAPE_ACTION = Input.Keys.ESCAPE;
    public static final int SPELL_BOOK = Input.Keys.P;
    public static final int FULLSCREEN = Input.Keys.F10;
    public static final int INVENTORY_WINDOW = Input.Keys.B;
    public static final int EQUIPMENT_WINDOW = Input.Keys.C;

    /*
     * INTERACTION
     */
    public static final int INTERACT = Input.Keys.E;
    public static final int ACTION_1 = Input.Keys.NUM_1;
    public static final int ACTION_2 = Input.Keys.NUM_2;
    public static final int ACTION_3 = Input.Keys.NUM_3;
    public static final int ACTION_4 = Input.Keys.NUM_4;
    public static final int ACTION_5 = Input.Keys.NUM_5;
    public static final int ACTION_6 = Input.Keys.NUM_6;
    public static final int ACTION_7 = Input.Keys.NUM_7;
    public static final int ACTION_8 = Input.Keys.NUM_8;
    public static final int ACTION_9 = Input.Keys.NUM_9;
    public static final int ACTION_10 = Input.Keys.NUM_0;

    public static String printKey(int key) {
        return Input.Keys.toString(key);
    }
}
