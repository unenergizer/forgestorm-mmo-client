package com.valenguard.client.game.scripting;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;

public class NPCTextDialog extends HideableVisWindow implements Buildable {

    private VisTable table = new VisTable();

    public NPCTextDialog() {
        super("Chatting");
    }

    @Override
    public Actor build() {

        setResizable(false);
        centerWindow();

        table.pad(3);
        add(table);
        setWidth(200);
        setHeight(100);

        setVisible(false);
        return this;
    }

    private void clean() {
        table.clearChildren();
    }

    public void say(String npcName, String formattedMessage) {

        ActorUtil.fadeInWindow(this);

        StringBuilder builder = new StringBuilder(npcName).append(" ").append(formattedMessage);

        clean();
        VisLabel textLabel = new VisLabel(builder.toString());
        table.add(textLabel);

    }
}
