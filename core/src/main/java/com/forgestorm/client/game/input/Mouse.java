package com.forgestorm.client.game.input;

import com.badlogic.gdx.InputProcessor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;

public class Mouse implements InputProcessor {
    
    private final ClientMain clientMain;
    private int buttonDown;

    public Mouse(ClientMain clientMain) {
        this.clientMain = clientMain;
    }
    
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
        if (clientMain.getUserInterfaceType() != UserInterfaceType.GAME) return false;
        if (!clientMain.getGameScreen().isGameFocused()) return false;
        buttonDown = button;

        ChatWindow chatWindow = clientMain.getStageHandler().getChatWindow();
        if (!chatWindow.isWindowFaded()) chatWindow.toggleChatWindowInactive(true, true);

        clientMain.getMouseManager().mouseClick(screenX, screenY, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        clientMain.getMouseManager().mouseDragged(buttonDown, screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        clientMain.getMouseManager().mouseMove(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        clientMain.getGameScreen().getCamera().scrollZoomLevel((int) amountY);
        return false;
    }
}
