package com.forgestorm.client.game.input;

import com.badlogic.gdx.InputProcessor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.game.ChatWindow;

public class Mouse implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (ClientMain.getInstance().getUserInterfaceType() != UserInterfaceType.GAME) return false;
        if (!ClientMain.gameScreen.isGameFocused()) return false;

        ChatWindow chatWindow = ActorUtil.getStageHandler().getChatWindow();
        if (!chatWindow.isWindowFaded()) chatWindow.toggleChatWindowInactive(true, true);

        ClientMain.getInstance().getMouseManager().mouseClick(screenX, screenY, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        ClientMain.getInstance().getMouseManager().mouseMove(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        ClientMain.gameScreen.getCamera().scrollZoomLevel(amount);
        return false;
    }
}
