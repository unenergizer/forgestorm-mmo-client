package com.valenguard.client.game.screens.ui.actors.dialogue;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class ChatDialogue extends HideableVisWindow implements Buildable {

    private VisTextButton button;
    private TypingLabel messageDialogue;

    public ChatDialogue() {
        super("");
    }

    public void drawText(String text) {
        if (!this.isVisible()) setVisible(true);
        messageDialogue.restart(text);
        pack();
        centerWindow();
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        button = new VisTextButton("->");
        messageDialogue = new TypingLabel("", VisUI.getSkin());

        add(messageDialogue).align(Alignment.TOP_LEFT.getAlignment()).row();
        add(button).align(Alignment.BOTTOM_RIGHT.getAlignment());
        setVisible(false);
        pack();
        centerWindow();
        return this;
    }
}
