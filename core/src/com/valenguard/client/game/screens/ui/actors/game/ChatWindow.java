package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.ClientMain;
import com.valenguard.client.game.GameQuitReset;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.out.ChatMessagePacketOut;

import java.util.Stack;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

@Getter
public class ChatWindow extends HideableVisWindow implements Buildable, GameQuitReset {

    private static final String ENTER_MESSAGE = "Press Enter to send a message...";

    private StageHandler stageHandler;
    private ChatWindow chatWindow;
    private VisScrollPane scrollPane;
    private VisTable messageTable;
    private VisTextField messageInput;

    /**
     * Determines if the chat area should be listening to text input.
     */
    private boolean chatToggled = false;
    private boolean windowFaded = false;

    private Stack<String> previousMessages = new Stack<String>();
    private int previousMessageIndex = -1;
    private String currentBufferString = "";

    public ChatWindow() {
        super("");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        this.chatWindow = this;

        final int innerPadding = 5;
        pad(innerPadding);
        setResizable(true);
        setWidth(350);
        setHeight(150);

        VisImageButton chatMenuButton = new VisImageButton(new ImageBuilder(GameAtlas.ITEMS, "skill_156").buildTextureRegionDrawable(), "Chat Menu");

        messageTable = new VisTable();

        messageInput = new VisTextField(ENTER_MESSAGE, "chat-box");
        messageInput.setFocusTraversal(false);
        messageInput.setMaxLength(127); // Max chat length is 0x7F.

        scrollPane = new VisScrollPane(messageTable);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(true);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setScrollingDisabled(true, false);

        // Prevent client from typing in message area
        messageTable.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stageHandler.getStage().setKeyboardFocus(null);
                Gdx.input.setOnscreenKeyboardVisible(false);
                return false;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                stageHandler.getStage().setKeyboardFocus(null);
                Gdx.input.setOnscreenKeyboardVisible(false);
                return true;
            }
        });

        // Toggled input via mouse
        messageInput.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                toggleChatWindowActive(true);
                return true;
            }
        });

        // Toggled chat button click
        chatMenuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // TODO: Remove lines below. Add pop-up menu like wow to interact with chat.
                toggleChatWindowActive(true);
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(ChatWindow.class, (short) 0);
                return true;
            }
        });

        // This main listener. Check for the enter key (chat toggle) here.
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                if (keycode == Input.Keys.UP && chatToggled) {
                    scrollUpThroughPreviousMessages();
                    return false;
                }

                if (keycode == Input.Keys.DOWN && chatToggled) {
                    scrollDownThroughPreviousMessages();
                    return false;
                }

                if (keycode == Input.Keys.ENTER || (keycode == Input.Keys.ESCAPE && chatToggled)) {

                    previousMessageIndex = -1;

                    if (chatToggled && keycode != Input.Keys.ESCAPE) {
                        // The player hit the enter key, his message will now be sent!
                        // We also clear the message input
                        String message = messageInput.getText();
                        if (!message.isEmpty()) {
                            if (previousMessages.size() == ClientConstants.MAX_PREVIOUS_SCROLL_MESSAGES) {
                                Stack<String> newStack = new Stack<String>();
                                for (int i = 1; i < previousMessages.size(); i++) {
                                    newStack.push(previousMessages.get(i));
                                }
                                previousMessages = newStack;
                            }
                            previousMessages.push(message);

                            currentBufferString = "";
                            new ChatMessagePacketOut(message).sendPacket();
                        }

                        // Clear the players message.
                        toggleChatWindowInactive(false, true);
                    } else if (chatToggled) {
                        // Player was typing a message but hit the escape key.
                        // Reset the focus back to the stage and save players message.
                        toggleChatWindowInactive(false, false);
                    } else {
                        println(ChatWindow.class, "Something should happen here???", true);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isWindowFaded()) toggleChatWindowActive(true);
                return true;
            }
        });

        add(scrollPane).colspan(2).growX().expandY().top();
        row();
        add(chatMenuButton).padRight(3);
        add(messageInput).expandX().fillX().padTop(3);

//        pack();
        findPosition();

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        setVisible(false);
        toggleChatWindowInactive(true, true);
        return this;
    }

    private void findPosition() {
        ExperienceBar experienceBar = stageHandler.getExperienceBar();
        float endPosition = getX() + getWidth();

        // If the chat box overlaps the experience bar, raise the chat box position up.
        if (endPosition > experienceBar.getX()) {
            setPosition(StageHandler.WINDOW_PAD_X, experienceBar.getY() + experienceBar.getHeight() + StageHandler.WINDOW_PAD_Y);
        } else {
            setPosition(StageHandler.WINDOW_PAD_X, StageHandler.WINDOW_PAD_Y);
        }
    }

    public void toggleChatWindowActive(boolean clearText) {
        chatToggled = true;
        windowFaded = false;
        chatWindow.getColor().a = .8f;
        if (clearText) messageInput.setText("");
        FocusManager.switchFocus(stageHandler.getStage(), messageInput);
        stageHandler.getStage().setKeyboardFocus(messageInput);
    }

    public void toggleChatWindowInactive(boolean fadeOutWindow, boolean setDefaultInputText) {
        chatToggled = false;
        if (fadeOutWindow) {
            windowFaded = true;
            chatWindow.getColor().a = .5f;
        }
        if (setDefaultInputText) messageInput.setText(ENTER_MESSAGE);
        Gdx.input.setOnscreenKeyboardVisible(false);
        FocusManager.resetFocus(stageHandler.getStage());
    }

    private void scrollUpThroughPreviousMessages() {
        if (previousMessages.isEmpty()) return;
        if (previousMessageIndex == -1) {
            currentBufferString = messageInput.getText();
        }

        previousMessageIndex = Math.min(previousMessageIndex + 1, previousMessages.size() - 1);

        String displayMessage = previousMessages.get(previousMessages.size() - 1 - previousMessageIndex);

        setMessage(displayMessage);
    }

    private void scrollDownThroughPreviousMessages() {
        if (previousMessages.isEmpty()) return;
        previousMessageIndex = Math.max(-1, previousMessageIndex - 1);
        if (previousMessageIndex == -1) {
            setMessage(currentBufferString);
            return;
        }

        String displayMessage = previousMessages.get(previousMessages.size() - 1 - previousMessageIndex);
        setMessage(displayMessage);
    }

    private void setMessage(String message) {
        messageInput.setText(message);
        messageInput.setCursorAtTextEnd();
    }

    public void appendChatMessage(String message) {
        VisLabel label = new VisLabel(message, stageHandler.getMarkupStyle());
        label.setWrap(true);
        messageTable.add(label).expandX().fillX().expandY().top().row();

        scrollPane.layout();
        scrollPane.scrollTo(0, 0, 0, 0);
    }

    @Override
    public void gameQuitReset() {
        messageTable.clear();
    }
}
