package com.forgestorm.client.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.TimeUtils;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;

public class LongPressListener extends InputAdapter {

    private final ClientMain clientMain;
    private long touchDownTime = 0;
    private boolean isTouching = false;

    private int screenX, screenY;

    // Constructor with custom duration
    public LongPressListener(ClientMain clientMain) {
        this.clientMain = clientMain;
        Gdx.app.log(LongPressListener.class.getName(), "Listener started!");
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (clientMain.getUserInterfaceType() != UserInterfaceType.GAME) return false;
        if (!clientMain.getGameScreen().isGameFocused()) return false;

        ChatWindow chatWindow = clientMain.getStageHandler().getChatWindow();
        if (!chatWindow.isWindowFaded()) chatWindow.toggleChatWindowInactive(true, true);

        // fake a mouse move with touch down, this is needed to trigger drop down menus
        clientMain.getMouseManager().mouseMove(screenX, screenY);

        Gdx.app.log(LongPressListener.class.getName(), "Begin long press!");
        touchDownTime = TimeUtils.millis();
        isTouching = true;
        this.screenX = screenX;
        this.screenY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isTouching = false;
        Gdx.app.log(LongPressListener.class.getName(), "Long press canceled!");
        return true;
    }

    public void update() {
        if (isTouching) {
            long time = TimeUtils.millis() - touchDownTime;
            // default is 1 second
            long longPressMillis = 250;
            Gdx.app.log(LongPressListener.class.getName(), "Touch time: " + time + ", LongPressMillis: " + longPressMillis);
            if (TimeUtils.millis() - touchDownTime >= longPressMillis) {
                Gdx.app.log(LongPressListener.class.getName(), "Calling long press?");
                isTouching = false; // ensure the action happens only once
                onLongPress();
            }
        }
    }

    // This method will be called when a long press is detected
    public void onLongPress() {
        Gdx.app.log(LongPressListener.class.getName(), "Trigger long press action!");
        clientMain.getMouseManager().mouseClick(screenX, screenY, Input.Buttons.RIGHT); // Simulate right click
    }
}

