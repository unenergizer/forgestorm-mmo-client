package com.valenguard.client.game.screens.stage.game;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.stage.AbstractUI;
import com.valenguard.client.network.packet.out.SendChatMessage;

import lombok.Getter;
import lombok.Setter;

public class ChatBox extends AbstractUI implements TextField.TextFieldListener {

    private TextArea chatArea;

    @Getter
    private TextField chatField;

    @Setter
    @Getter
    private boolean preventInput = false;

    @Override
    public void build(Skin skin) {
        Table table = new Table();
        table.setFillParent(true);
        table.setColor(0, 0, 0, .5f);

        addActor(table);
        setWidth(300);
        setHeight(200);

        chatArea = new TextArea(null, skin);
        ScrollPane scrollPane = new ScrollPane(chatArea, skin);
        chatField = new TextField(null, skin);
        chatField.setFocusTraversal(false);
        chatField.setTextFieldListener(this);

        table.add(scrollPane).expandX().expandY().fill();
        table.row().padTop(3);
        table.add(chatField).expandX().fill();
    }

    @Override
    public void dispose() {

    }

    public void updateChatBox(String chatMessage) {
        chatArea.appendText("\n" + chatMessage);
    }


    @Override
    public void keyTyped(TextField textField, char c) {

        if (preventInput) return; // not sending message until next enter/return key stroke
        if (c == '\t') return; // cancel tab
        if (c == '\n' || c == '\r') { //user hit enter
            // send message down the wire
            new SendChatMessage(chatField.getText().replace(Character.toString('\n'), "")).sendPacket();

            // clear typed text for next message & clear input focus after message sent
            chatField.setText("");
            Valenguard.getInstance().getUiManager().getStage().setKeyboardFocus(null);
        }
    }
}