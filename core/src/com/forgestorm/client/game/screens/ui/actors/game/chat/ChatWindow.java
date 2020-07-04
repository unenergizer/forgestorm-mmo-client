package com.forgestorm.client.game.screens.ui.actors.game.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.GameQuitReset;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.ExperienceBar;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.ChatMessagePacketOut;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

@Getter
public class ChatWindow extends HideableVisWindow implements Buildable, GameQuitReset {

    private static final String ENTER_MESSAGE = "Press Enter to send a message...";

    private StageHandler stageHandler;
    private ChatWindow chatWindow;
    private VisTextField messageInput;

    /**
     * Determines if the chat area should be listening to text input.
     */
    private boolean chatToggled = false;
    private boolean windowFaded = false;

    private Stack<String> previousMessages = new Stack<String>();
    private int previousMessageIndex = -1;
    private String currentBufferString = "";

    private List<ChatChannel> chatChannelList = new ArrayList<ChatChannel>();

    private VisTable channelTable;
    private VisTable chatChannelWrapperTable;

    // Active Chat Channel
    private ChatChannel activeChatChannel;

    public ChatWindow() {
        super("");
    }

    public void showChannel(ChatChannelType chatChannelType) {
        for (ChatChannel chatChannel : chatChannelList) {
            if (chatChannel.chatChannelType == chatChannelType) chatChannel.setVisible(true);
        }
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

        // Build test buttons
        channelTable = new VisTable();

        // Build channels
        chatChannelWrapperTable = new VisTable();

        chatChannelList.add(new ChatChannel(ChatChannelType.GENERAL).build(stageHandler));
        chatChannelList.add(new ChatChannel(ChatChannelType.COMBAT).build(stageHandler));
        chatChannelList.add(new ChatChannel(ChatChannelType.TRADE).build(stageHandler));

        // Add all chat channels to the window
        activeChatChannel = chatChannelList.get(0);
        chatChannelWrapperTable.add(chatChannelList.get(0)).growX().expandY();

//        for (ChatChannel chatChannel : chatChannelList) {
//            chatChannelWrapperTable.add(chatChannel).growX().expandY();
//        }

        VisImageButton chatMenuButton = new VisImageButton(new ImageBuilder(GameAtlas.ITEMS, "skill_156").buildTextureRegionDrawable(), "Chat Menu");

        messageInput = new VisTextField(ENTER_MESSAGE, "chat-box");
        messageInput.setFocusTraversal(false);
        messageInput.setMaxLength(127); // Max chat length is 0x7F.

        // Build Window
        add(channelTable).colspan(2).align(Alignment.LEFT.getAlignment());
        row();
        add(chatChannelWrapperTable).colspan(2).growX().expandY().top();
        row();
        add(chatMenuButton).padRight(3);
        add(messageInput).expandX().fillX().padTop(3);

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
                            new ChatMessagePacketOut(activeChatChannel.chatChannelType, message).sendPacket();
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

        setInputMessage(displayMessage);
    }

    private void scrollDownThroughPreviousMessages() {
        if (previousMessages.isEmpty()) return;
        previousMessageIndex = Math.max(-1, previousMessageIndex - 1);
        if (previousMessageIndex == -1) {
            setInputMessage(currentBufferString);
            return;
        }

        String displayMessage = previousMessages.get(previousMessages.size() - 1 - previousMessageIndex);
        setInputMessage(displayMessage);
    }

    private void setInputMessage(String message) {
        messageInput.setText(message);
        messageInput.setCursorAtTextEnd();
    }

    public void appendChatMessage(ChatChannelType chatChannelType, String chatMessage) {
        for (ChatChannel chatChannel : chatChannelList) {
            if (chatChannel.chatChannelType == chatChannelType)
                chatChannel.appendChatMessage(chatMessage);
        }
    }

    @Override
    public void gameQuitReset() {
        println(getClass(), "GameQuitReset....");
        for (ChatChannel chatChannel : chatChannelList) {
            chatChannel.gameQuitReset();
        }
//        chatChannelList.clear();
    }

    private class ChatChannel extends VisTable implements Buildable, GameQuitReset {

        private final ChatChannel chatChannel;
        private final ChatChannelType chatChannelType;
        private VisScrollPane scrollPane;
        private VisTable messageTable;

        private VisTextButton channelButton;
        private int unreadMessages;

        ChatChannel(ChatChannelType chatChannelType) {
            this.chatChannel = this;
            this.chatChannelType = chatChannelType;
        }

        public void appendChatMessage(String message) {
            println(getClass(), "IsVisible: " + chatChannel.isVisible() + ", ChannelType: " + chatChannelType + ", Message: " + message);
            VisLabel label = new VisLabel(message, stageHandler.getMarkupStyle());
            label.setWrap(true);
            messageTable.add(label).expandX().fillX().expandY().top().row();

            scrollPane.layout();
            scrollPane.scrollTo(0, 0, 0, 0);

            // Count unreadMessages
            if (activeChatChannel != chatChannel) {
                unreadMessages += 1;
                channelButton.setText(chatChannelType.name() + " +" + unreadMessages);
            }
        }

        public ChatChannel build(final StageHandler stageHandler) {
            messageTable = new VisTable();
            scrollPane = new VisScrollPane(messageTable);
            scrollPane.setOverscroll(false, false);
            scrollPane.setFlickScroll(false);
            scrollPane.setFadeScrollBars(true);
            scrollPane.setScrollbarsOnTop(true);
            scrollPane.setScrollingDisabled(true, false);
            add(scrollPane).growX().expandY().top();

            // Add chat button
            channelButton = new VisTextButton(chatChannelType.name());
            channelTable.add(channelButton).padRight(3);

            channelButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    // Chat channel click. Lets set the correct chat channel
                    if (activeChatChannel != chatChannel) {
                        // Clear current chat channel
                        chatChannelWrapperTable.removeActor(activeChatChannel); // Remove without clearing
                        chatChannelWrapperTable.clear(); // Clears previous table formatting

                        // Setup this channel
                        activeChatChannel = chatChannel;
                        chatChannelWrapperTable.add(chatChannel).growX().expandY();

                        // Do scrolling...
                        scrollPane.layout();
                        scrollPane.scrollTo(0, 0, 0, 0);

                        // Remove unread messages
                        unreadMessages = 0;
                        channelButton.setText(chatChannelType.name());
                    }
                    return false;
                }
            });

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
            return this;
        }

        @Override
        public void gameQuitReset() {
            messageTable.clearChildren();
        }
    }
}
