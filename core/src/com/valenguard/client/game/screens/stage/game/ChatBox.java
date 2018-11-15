package com.valenguard.client.game.screens.stage.game;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.stage.AbstractUI;
import com.valenguard.client.network.packet.out.SendChatMessage;

public class ChatBox extends AbstractUI {

    private final float height = 200;
    private final float width = 300;
    private final Table table = new Table();
    private TextArea chatArea;
    private ScrollPane scrollPane;
    private TextField chatField;
    private String chatMessage;

    @Override
    public void build(Skin skin) {
        addActor(table);
        table.setFillParent(true);
        setWidth(width);
        setHeight(height);
//        table.setPosition(0,0);

        chatArea = new TextArea(null, skin);
        scrollPane = new ScrollPane(chatArea, skin);
        chatField = new TextField(null, skin);
        chatField.setFocusTraversal(false);
        chatField.setTextFieldListener(new ChatInput());

        table.add(scrollPane).expandX().expandY().fill();
        table.row().padTop(3);
        table.add(chatField).expandX().fill();
    }

    @Override
    public void dispose() {

    }

    public void updateChatBox(String chatMessage) {
        chatArea.appendText(chatMessage);
    }

    private class ChatInput implements TextField.TextFieldListener {
        @Override
        public void keyTyped(TextField textField, char c) {
            if (c == '\t') return; // cancel tab
            if (c == '\n' || c == '\r') { //user hit enter

                String msg = chatField.getText();

                chatMessage = ""; // clear previous chat message
                chatMessage = msg;

                // send message down the wire
                new SendChatMessage(chatMessage).sendPacket();

                // clear typed text for next message
                chatField.setText("");

                // clear input focus after message sent
                Valenguard.getInstance().getUiManager().getStage().setKeyboardFocus(null);
            }
        }
    }
}
