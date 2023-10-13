package com.forgestorm.client.game.screens.ui.actors.dialogue;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ChatDialogue extends HideableVisWindow implements Buildable {

    private VisTextButton button;
//    private TypingLabel messageDialogue;

    public ChatDialogue(ClientMain clientMain) {
        super(clientMain, "");
    }

    public void drawText(String text) {
//        if (!this.isVisible()) setVisible(true);
//        messageDialogue.restart(text);
//        pack();
//        centerWindow();
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
//        button = new VisTextButton("->");
//        messageDialogue = new TypingLabel("", VisUI.getSkin());

//        add(messageDialogue).align(Alignment.TOP_LEFT.getAlignment()).row();
//        add(button).align(Alignment.BOTTOM_RIGHT.getAlignment());
//        setVisible(false);
//        pack();
//        centerWindow();
        return this;
    }
}
