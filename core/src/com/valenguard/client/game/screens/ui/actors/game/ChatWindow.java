package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.network.game.packet.out.ChatMessagePacketOut;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;

@Getter
public class ChatWindow extends HideableVisWindow implements Buildable, Focusable {

    private static final String ENTER_MESSAGE = "Press Enter to send a message...";
    private TextArea messagesDisplay;
    private VisTextField messageInput;

    /**
     * Determines if the chat area should be listening to text input.
     */
    @Setter
    private boolean chatToggled = false;

    /**
     * Used to prevent the Window from crashing due to a "line return" being the first message drawn.
     * Issue: https://github.com/libgdx/libgdx/issues/5319
     * Note: The issue is marked as solved, but apparently still happens.
     * <p>
     * UPDATE: This may not be valid anymore as we are using VisWindow components. TODO: Need to retest.
     */
    private boolean displayEmpty = true;

    public ChatWindow() {
        super("", "chat-box");
    }

    @Override
    public Actor build() {
        final StageHandler stageHandler = ActorUtil.getStageHandler();
        final int innerPadding = 5;
        pad(innerPadding);
        setResizable(true);
        setPosition(0, 0);
        setWidth(350);
        setHeight(150);

        VisImageButton chatMenu = new VisImageButton(new ImageBuilder(GameAtlas.ITEMS, "skill_156").buildTextureRegionDrawable(), "Chat Menu");

        messagesDisplay = new TextArea(null, VisUI.getSkin(), "chat-box");
        ScrollPane scrollPane = new ScrollPane(messagesDisplay, VisUI.getSkin());
        messageInput = new VisTextField(ENTER_MESSAGE, "chat-box");
        messageInput.setFocusTraversal(false);
        messageInput.setMaxLength(256);

        scrollPane.setOverscroll(false, false);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setScrollingDisabled(true, false);

        // Prevent client from typing in message area
        messagesDisplay.addListener(new InputListener() {
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
                chatToggled = true;
                messageInput.setText("");
                FocusManager.switchFocus(stageHandler.getStage(), messageInput);
                stageHandler.getStage().setKeyboardFocus(messageInput);
                return true;
            }
        });

        // Toggled chat button click
        chatMenu.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // TODO: Remove lines below. Add pop-up menu like wow to interact with chat.
                chatToggled = true;
                messageInput.setText("");
                FocusManager.switchFocus(stageHandler.getStage(), messageInput);
                stageHandler.getStage().setKeyboardFocus(messageInput);
                return true;
            }
        });

        // This main listener. Check for the enter key (chat toggle) here.
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER || (keycode == Input.Keys.ESCAPE && chatToggled)) {

                    if (chatToggled && keycode != Input.Keys.ESCAPE) {
                        // The player hit the enter key, his message will now be sent!
                        // We also clear the message input
                        String message = messageInput.getText();
                        if (!message.isEmpty()) new ChatMessagePacketOut(message).sendPacket();
                        messageInput.setText(ENTER_MESSAGE);
                        chatToggled = false;
                        Gdx.input.setOnscreenKeyboardVisible(false);
                        FocusManager.resetFocus(stageHandler.getStage());
                    } else if (chatToggled) {
                        // Player was typing a message but hit the escape key.
                        // Reset the focus back to the stage and save players message.
                        chatToggled = false;
                        Gdx.input.setOnscreenKeyboardVisible(false);
                        FocusManager.resetFocus(stageHandler.getStage());
                    } else {
                        println(ChatWindow.class, "Something should happen here???", true);
//                        chatToggled = true;
//                        stageHandler.getStage().setKeyboardFocus(messageInput);
                    }
                    return true;
                }
                return false;
            }
        });

        add(messagesDisplay).colspan(2).grow().expand().fill();
        row();
        add(chatMenu).padRight(3);
        add(messageInput).expandX().fillX().padTop(3);
        setVisible(false);
        return this;
    }

    public void appendChatMessage(String message) {
        if (displayEmpty) {
            displayEmpty = false;
            messagesDisplay.appendText(message);
        } else {
            // Put the "line return" BEFORE the message to make sure the window
            // does not have a blank line (line return) as the bottom message.
            messagesDisplay.appendText("\n" + message);
        }
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }
}
